use serde::{Deserialize, Serialize};

#[derive(Clone, Debug, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct AdapterStatus {
    pub available: bool,
    pub name: Option<String>,
    pub powered: bool,
    pub discovering: bool,
}

#[derive(Clone, Debug, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct BleDevice {
    pub address: String,
    pub name: Option<String>,
    pub rssi: Option<i16>,
    pub service_uuids: Vec<String>,
    pub connected: bool,
    pub paired: bool,
    pub trusted: bool,
}

#[derive(Clone, Debug, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct ScanStartParams {
    pub timeout_ms: Option<u64>,
    pub name_prefix: Option<String>,
    pub service_uuid: Option<String>,
}

impl Default for ScanStartParams {
    fn default() -> Self {
        Self {
            timeout_ms: Some(8_000),
            name_prefix: None,
            service_uuid: None,
        }
    }
}
