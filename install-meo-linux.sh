#!/bin/bash

# This script installs necessary infrastructure for MEO Open Service on Linux systems.
echo "Starting installation of MEO Open Service infrastructure..."

# Update package lists
sudo apt-get update
if [ $? -ne 0 ]; then
    echo "Failed to update package lists. Please check your network connection."
    exit 1
fi

# Install Mosquitto MQTT broker
sudo apt-get install -y mosquitto mosquitto-clients
if [ $? -ne 0 ]; then
    echo "Failed to install Mosquitto. Please check your package manager settings."
    exit 1
fi

# Enable and start Mosquitto service
sudo systemctl enable mosquitto
sudo systemctl start mosquitto
if [ $? -ne 0 ]; then
    echo "Failed to start Mosquitto service."
    exit 1
fi

# Install InfluxDB
wget -q https://repos.influxdata.com/influxdata-archive_compat.key
echo "393e8779c89ac8d958f81f942f9ad7fb82a25e133faddaf92e15b16e6ac9ce4c influxdata-archive_compat.key" | sha256sum -c && cat influxdata-archive_compat.key | gpg --dearmor | sudo tee /etc/apt/trusted.gpg.d/influxdata-archive_compat.gpg > /dev/null
echo 'deb [signed-by=/etc/apt/trusted.gpg.d/influxdata-archive_compat.gpg] https://repos.influxdata.com/debian stable main' | sudo tee /etc/apt/sources.list.d/influxdata.list
sudo apt update
sudo apt-get update && sudo apt-get install influxdb2

if [ $? -ne 0 ]; then
    echo "Failed to install InfluxDB."
    exit 1
fi

# Enable and start InfluxDB service
sudo systemctl enable influxdb
sudo systemctl start influxdb
if [ $? -ne 0 ]; then
    echo "Failed to start InfluxDB service."
    exit 1
fi

## Configure InfluxDB (example configuration, adjust as needed)
#influxd setup --username meo --password meo123456 --org meo_org --bucket meo_bucket --retention 30d --force
#if [ $? -ne 0 ]; then
#    echo "Failed to configure InfluxDB."
#    exit 1
#fi
#
## Get InfluxDB token for later use
#INFLUXDB_TOKEN=$(influxd auth list -o meo_org -t $(influxd config get -o) | grep meo | awk '{print $1}')
#if [ -z "$INFLUXDB_TOKEN" ]; then
#    echo "Failed to retrieve InfluxDB token."
#    exit 1
#fi

export INFLUXDB_TOKEN
echo "InfluxDB token set in environment variable."

# Store InfluxDB token in a file for application in /etc/meo/open-service.conf
echo "INFLUXDB_TOKEN=$INFLUXDB_TOKEN" | sudo tee -a /etc/meo/open-service.conf

# Install Node-RED
sudo apt-get install -y build-essential git curl
if [ $? -ne 0 ]; then
    echo "Failed to install prerequisites for Node-RED."
    exit 1
fi

bash <(curl -sL https://raw.githubusercontent.com/node-red/linux-installers/master/deb/update-nodejs-and-nodered)
if [ $? -ne 0 ]; then
    echo "Failed to install Node-RED."
    exit 1
fi

# Enable and start Node-RED service
sudo systemctl enable nodered.service
sudo systemctl start nodered.service
if [ $? -ne 0 ]; then
    echo "Failed to start Node-RED service."
    exit 1
fi

# Final message
echo "MEO Open Service infrastructure installation completed successfully!"
echo "Mosquitto, InfluxDB, and Node-RED are now installed and running."
echo "You can access Node-RED at http://localhost:1880"
