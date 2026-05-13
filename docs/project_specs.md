# MEO 3 Project Specs

> This document is a living project note. The information here may become deprecated as the hardware, software, service architecture, and learning experience evolve.

## Project Overview

MEO 3, short for Make Everything Online, is an IoT project for STEAM education. It explores how IoT-enabled hardware and software can be delivered in a way that is easy to use, customizable, and friendly for children.

This repository contains the open service layer. The current direction is to pair the service with Node-RED so devices, automations, and learning activities can be created with less code. The service is expected to run locally on gateway-style hardware and act as a bridge between MEO-compatible devices, automation tools, and future user interfaces.

## Goals

- Make IoT experimentation easier for children, teachers, and makers.
- Support custom hardware while keeping the user experience approachable.
- Provide a local service that can discover, register, manage, and communicate with devices.
- Pair with Node-RED or similar low-code tools for visual automation.
- Keep the system flexible enough for classroom activities, experiments, and future UI changes.
- Hide unnecessary technical details from child-facing experiences while keeping advanced controls available for makers and developers.

## Vision

MEO 3 should feel like a learning tool first and an IoT platform second. Children should be able to connect a device, observe real-world data, create simple cause-and-effect automations, and understand what is happening without needing to understand networking, MQTT topics, payload formats, or service internals.

For teachers, the project should make classroom setup and activity design easier. For makers, it should remain open enough to support custom firmware, new sensors, new actuators, and local integrations.

The long-term direction is a local-first, education-focused IoT environment where hardware, software, and learning activities work together as one experience.

## UI/UX Direction

The future UI may take inspiration from PocketLab-style learning tools: visual, direct, experiment-focused, and friendly for young learners.

Guiding principles:

- Show devices as understandable physical objects, not network resources.
- Prefer clear names like "Temperature Sensor" or "Light Controller" over IDs and protocol terms.
- Present data through live readings, graphs, experiment cards, and simple actions.
- Make automations feel like cause-and-effect statements: "When this changes, do that."
- Keep child-facing screens simple, playful, and safe.
- Provide separate advanced views for debugging, custom firmware, MQTT details, and service configuration.
- Allow the UI philosophy to change as the project learns from real use.

## Open Questions

- What is the best first-time setup flow for children and teachers?
- Should device registration be fully local, cloud-assisted, or support both modes?
- How much of the automation model should live in Node-RED versus MEO itself?
- Should children, teachers, and makers use one adaptive UI or separate interfaces?
- How should custom hardware describe its features so the UI can show them clearly?
- What offline behavior is required for classrooms with unreliable internet?
- Which concepts should be visible to children, and which should stay in advanced mode?
