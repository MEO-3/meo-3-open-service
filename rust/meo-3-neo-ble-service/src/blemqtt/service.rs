use serde_json::{Value, json};

use crate::ble::{BleManager, ScanStartParams};
use crate::blemqtt::command::{BleMqttCommand, BleMqttOp};
use crate::blemqtt::reply::BleMqttReply;
use crate::log::Log;

const TAG: &str = "blemqtt-service";

pub struct BleMqttService {
    ble: BleManager,
    log: Log,
}

impl BleMqttService {
    pub fn new(ble: BleManager, log: Log) -> Self {
        Self { ble, log }
    }

    pub async fn handle_command(&self, command: BleMqttCommand) -> BleMqttReply {
        self.log.debug(
            TAG,
            &format!(
                "command request_id={} op={:?}",
                command.request_id, command.op
            ),
        );

        match command.op {
            BleMqttOp::AdapterStatus => self.adapter_status(command.request_id).await,
            BleMqttOp::AdapterPower => self.adapter_power(command.request_id, command.params).await,
            BleMqttOp::ScanStart => self.scan_start(command.request_id, command.params).await,
            BleMqttOp::DeviceList => self.device_list(command.request_id).await,
            op => BleMqttReply::error(
                command.request_id,
                "blemqtt.unsupported_op",
                format!("operation is not implemented yet: {op:?}"),
            ),
        }
    }

    async fn adapter_status(&self, request_id: String) -> BleMqttReply {
        match self.ble.adapter_status().await {
            Ok(status) => self.ok(request_id, json!(status)),
            Err(error) => self.err(request_id, "ble.adapter_status_failed", error.to_string()),
        }
    }

    async fn adapter_power(&self, request_id: String, params: Value) -> BleMqttReply {
        let enabled = match params.get("enabled").and_then(|value| value.as_bool()) {
            Some(enabled) => enabled,
            None => {
                return self.err(
                    request_id,
                    "blemqtt.invalid_params",
                    "adapter.power requires boolean params.enabled",
                );
            }
        };

        match self.ble.adapter_power(enabled).await {
            Ok(status) => self.ok(request_id, json!(status)),
            Err(error) => self.err(request_id, "ble.adapter_power_failed", error.to_string()),
        }
    }

    async fn scan_start(&self, request_id: String, params: Value) -> BleMqttReply {
        let params = match serde_json::from_value::<ScanStartParams>(params) {
            Ok(params) => params,
            Err(error) => {
                return self.err(request_id, "blemqtt.invalid_params", error.to_string());
            }
        };

        match self.ble.scan_start(params).await {
            Ok(devices) => self.ok(request_id, json!({ "devices": devices })),
            Err(error) => self.err(request_id, "ble.scan_failed", error.to_string()),
        }
    }

    async fn device_list(&self, request_id: String) -> BleMqttReply {
        match self.ble.device_list().await {
            Ok(devices) => self.ok(request_id, json!({ "devices": devices })),
            Err(error) => self.err(request_id, "ble.device_list_failed", error.to_string()),
        }
    }

    fn ok(&self, request_id: String, result: Value) -> BleMqttReply {
        self.log
            .debug(TAG, &format!("reply ok request_id={request_id}"));
        BleMqttReply::ok(request_id, result)
    }

    fn err(
        &self,
        request_id: String,
        code: impl Into<String>,
        message: impl Into<String>,
    ) -> BleMqttReply {
        let code = code.into();
        let message = message.into();
        self.log.warning(
            TAG,
            &format!("reply error request_id={request_id} code={code} message={message}"),
        );
        BleMqttReply::error(request_id, code, message)
    }
}
