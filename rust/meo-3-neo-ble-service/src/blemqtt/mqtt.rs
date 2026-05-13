use std::time::Duration;

use rumqttc::v5::mqttbytes::{QoS, v5::Publish};
use rumqttc::v5::{AsyncClient, Event, EventLoop, Incoming};
use serde_json::json;

use crate::blemqtt::codec::{decode_command, encode_event, encode_reply};
use crate::blemqtt::event::BleMqttEvent;
use crate::blemqtt::service::BleMqttService;
use crate::blemqtt::topic::{COMMAND_TOPIC, EVENT_TOPIC, reply_topic};
use crate::log::Log;

pub type MqttResult<T> = Result<T, Box<dyn std::error::Error + Send + Sync>>;

pub struct BleMqttConfig {
    pub broker_host: String,
    pub broker_port: u16,
    pub client_id: String,
    pub qos: QoS,
}

impl Default for BleMqttConfig {
    fn default() -> Self {
        Self {
            broker_host: "localhost".to_string(),
            broker_port: 1883,
            client_id: "meo-3-neo-ble-service".to_string(),
            qos: QoS::AtLeastOnce,
        }
    }
}

pub struct BleMqttBridge {
    client: AsyncClient,
    event_loop: EventLoop,
    config: BleMqttConfig,
    service: BleMqttService,
    log: Log,
}

impl BleMqttBridge {
    pub fn new(
        client: AsyncClient,
        event_loop: EventLoop,
        config: BleMqttConfig,
        service: BleMqttService,
        log: Log,
    ) -> Self {
        Self {
            client,
            event_loop,
            config,
            service,
            log,
        }
    }

    pub async fn run(mut self) -> MqttResult<()> {
        self.client
            .subscribe(COMMAND_TOPIC, self.config.qos)
            .await?;
        self.log.info(
            "blemqtt",
            &format!("listening on {} via {}", COMMAND_TOPIC, self.broker_label()),
        );

        loop {
            match self.event_loop.poll().await {
                Ok(Event::Incoming(Incoming::Publish(message))) => {
                    self.handle_publish(message).await?;
                }
                Ok(_) => {}
                Err(error) => {
                    self.log
                        .warning("blemqtt", &format!("MQTT event loop error: {error}"));
                    tokio::time::sleep(Duration::from_secs(1)).await;
                }
            }
        }
    }

    async fn handle_publish(&self, message: Publish) -> MqttResult<()> {
        match decode_command(&message.payload) {
            Ok(command) => {
                let reply_topic = reply_topic(&command.request_id);
                self.log.debug(
                    "blemqtt",
                    &format!(
                        "received command topic={}",
                        String::from_utf8_lossy(&message.topic)
                    ),
                );
                let reply = self.service.handle_command(command).await;
                let payload = encode_reply(&reply)?;
                self.client
                    .publish(reply_topic, self.config.qos, false, payload)
                    .await?;
            }
            Err(error) => {
                self.log
                    .warning("blemqtt", &format!("invalid command: {error}"));
                let event = BleMqttEvent::new(
                    "command.invalid",
                    json!({
                        "topic": String::from_utf8_lossy(&message.topic),
                        "message": error.to_string(),
                    }),
                );
                let payload = encode_event(&event)?;
                self.client
                    .publish(EVENT_TOPIC, self.config.qos, false, payload)
                    .await?;
            }
        }

        Ok(())
    }

    fn broker_label(&self) -> String {
        format!("{}:{}", self.config.broker_host, self.config.broker_port)
    }
}
