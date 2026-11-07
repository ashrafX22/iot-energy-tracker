# IoT Device Simulator (`iot-simulator`)

A lightweight, realistic smart electric meter telemetry generator for the `IotEnergyTracker` platform. Simulates smart meters transmitting periodic or burst energy readings to `ingestion-service` and Apache Kafka.

---

## ⚡ Features
- **Realistic Diurnal Load Curve**: Low base consumption at night, morning spikes (~07:30), evening peaks (~19:30), Gaussian micro-fluctuations, and random high-draw appliance events.
- **Multiple Modes**:
  - `stream` (default): Continuous periodic telemetry from simulated meters.
  - `burst`: Rapid batch load test transmitting $N$ events across the device pool.
- **Dynamic Device Discovery**: Optionally queries `device-service` (`GET /devices`) to send data for actual registered device UUIDs.
- **Dual Execution**: Run directly via Python CLI or in Docker Compose via profiles.

---

## 🚀 Quick Start (Local)

### 1. Run with Python (Zero-dependency fallback)
```bash
# Continuous streaming (10 meters, sending every 3 seconds)
python3 iot-simulator/main.py

# Custom devices and interval
python3 iot-simulator/main.py --devices 50 --interval 2.0

# Burst mode: send 200 events rapidly across 10 devices
python3 iot-simulator/main.py --mode burst --count 200 --devices 10

# Attempt to fetch real registered device UUIDs from device-service
python3 iot-simulator/main.py --fetch-devices
```

---

## 🐳 Run in Docker Compose

The simulator is registered with the `simulator` profile in `docker-compose.yml` so it will **not** start automatically with default `docker-compose up`.

### Start the simulator in Docker:
```bash
docker-compose --profile simulator up -d --build iot-simulator
```

### View real-time logs:
```bash
docker logs -f iot-simulator
```

### Stop the simulator:
```bash
docker-compose --profile simulator stop iot-simulator
```
