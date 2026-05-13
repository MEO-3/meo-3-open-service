use std::collections::BTreeMap;
use std::time::{Duration, Instant};

use bluer::{Adapter, AdapterEvent, Address, Session};
use futures_util::StreamExt;

use crate::ble::device::{AdapterStatus, BleDevice, ScanStartParams};
use crate::ble::error::BleResult;
use crate::log::Log;

const TAG: &str = "ble";

pub struct BleManager {
    log: Log,
}

impl BleManager {
    pub fn new(log: Log) -> Self {
        Self { log }
    }

    pub async fn adapter_status(&self) -> BleResult<AdapterStatus> {
        let session = Session::new().await?;
        let adapter = match self.default_adapter_if_available(&session).await? {
            Some(adapter) => adapter,
            None => {
                self.log.warning(TAG, "no BLE adapter available");
                return Ok(AdapterStatus {
                    available: false,
                    name: None,
                    powered: false,
                    discovering: false,
                });
            }
        };

        let status = self.adapter_status_from_adapter(&adapter).await?;
        self.log.debug(TAG, &format!("adapter status: {status:?}"));
        Ok(status)
    }

    pub async fn adapter_power(&self, enabled: bool) -> BleResult<AdapterStatus> {
        let session = Session::new().await?;
        let adapter = session.default_adapter().await?;
        self.log.info(
            TAG,
            &format!("setting adapter {} powered={enabled}", adapter.name()),
        );
        adapter.set_powered(enabled).await?;
        self.adapter_status_from_adapter(&adapter).await
    }

    pub async fn scan_start(&self, params: ScanStartParams) -> BleResult<Vec<BleDevice>> {
        let timeout = Duration::from_millis(params.timeout_ms.unwrap_or(8_000));
        let name_prefix = params.name_prefix;
        let session = Session::new().await?;
        let adapter = session.default_adapter().await?;

        if !adapter.is_powered().await? {
            self.log.info(TAG, "powering adapter before scan");
            adapter.set_powered(true).await?;
        }

        self.log.info(
            TAG,
            &format!(
                "scan start adapter={} timeout_ms={} name_prefix={:?}",
                adapter.name(),
                timeout.as_millis(),
                name_prefix
            ),
        );

        let mut devices = BTreeMap::<String, BleDevice>::new();
        let mut stream = adapter.discover_devices().await?;
        let deadline = Instant::now() + timeout;

        loop {
            let now = Instant::now();
            if now >= deadline {
                break;
            }

            let remaining = deadline.saturating_duration_since(now);
            match tokio::time::timeout(remaining, stream.next()).await {
                Ok(Some(AdapterEvent::DeviceAdded(address))) => {
                    if let Some(device) = self
                        .read_device(&adapter, address, name_prefix.as_deref())
                        .await?
                    {
                        self.log.debug(TAG, &format!("scan device: {:?}", device));
                        devices.insert(device.address.clone(), device);
                    }
                }
                Ok(Some(_)) => {}
                Ok(None) => break,
                Err(_) => break,
            }
        }

        let devices: Vec<BleDevice> = devices.into_values().collect();
        self.log
            .info(TAG, &format!("scan finished devices={}", devices.len()));
        Ok(devices)
    }

    pub async fn device_list(&self) -> BleResult<Vec<BleDevice>> {
        let session = Session::new().await?;
        let adapter = session.default_adapter().await?;
        let addresses = adapter.device_addresses().await?;
        let mut devices = Vec::with_capacity(addresses.len());

        for address in addresses {
            if let Some(device) = self.read_device(&adapter, address, None).await? {
                devices.push(device);
            }
        }

        self.log
            .debug(TAG, &format!("device list count={}", devices.len()));
        Ok(devices)
    }

    async fn default_adapter_if_available(&self, session: &Session) -> BleResult<Option<Adapter>> {
        if session.adapter_names().await?.is_empty() {
            return Ok(None);
        }
        Ok(Some(session.default_adapter().await?))
    }

    async fn adapter_status_from_adapter(&self, adapter: &Adapter) -> BleResult<AdapterStatus> {
        Ok(AdapterStatus {
            available: true,
            name: Some(adapter.name().to_string()),
            powered: adapter.is_powered().await?,
            discovering: adapter.is_discovering().await?,
        })
    }

    async fn read_device(
        &self,
        adapter: &Adapter,
        address: Address,
        name_prefix: Option<&str>,
    ) -> BleResult<Option<BleDevice>> {
        let device = adapter.device(address)?;
        let name = device.name().await.unwrap_or(None);

        if let Some(prefix) = name_prefix {
            if !name.as_deref().unwrap_or_default().starts_with(prefix) {
                return Ok(None);
            }
        }

        Ok(Some(BleDevice {
            address: address.to_string(),
            name,
            rssi: device.rssi().await.unwrap_or(None),
            connected: device.is_connected().await.unwrap_or(false),
            paired: device.is_paired().await.unwrap_or(false),
            trusted: device.is_trusted().await.unwrap_or(false),
        }))
    }
}
