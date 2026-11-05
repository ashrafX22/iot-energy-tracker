# IoT Simulator — Run & Modes Guide 🚀

A quick reference guide for running the IoT telemetry simulator locally or with Docker, including all operational modes and command-line flags.

---

## 1. Quick Overview of Modes

The simulator supports two execution modes:

| Mode | Flag | Description | Best For |
| :--- | :--- | :--- | :--- |
| **Stream** *(Default)* | `--mode stream` | Continuously transmits telemetry at regular time intervals until stopped (`Ctrl + C`). | Normal development, live dashboards, real-time Kafka event streaming. |
| **Burst** | `--mode burst` | Fires a fixed number of events (`--count`) as fast as possible, then exits. | Stress testing, benchmarking throughput, bulk event generation. |

---

## 2. Option A: Running Directly with Python

Make sure your virtual environment (or Python 3 environment) is active:

```bash
cd iot-simulator-service
```

> **Note:** The simulator requires `requests` (included in `requirements.txt`), but will automatically fall back to Python's built-in `urllib` if `requests` is not installed.

### 2.1. Stream Mode Examples

**1. Run with defaults (10 simulated devices, sends every 3.0 seconds):**
```bash
python3 main.py
```

**2. Fast stream with more devices (50 devices every 1.0 second):**
```bash
python3 main.py --devices 50 --interval 1.0
```

**3. Stream using REAL device UUIDs from `device-service`:**
```bash
python3 main.py --fetch-devices --interval 2.0
```
*(If `device-service` is unreachable or has no devices, it automatically falls back to synthetic `meter-XXX` IDs).*

---

### 2.2. Burst Mode Examples

**1. Send 100 events rapidly across default 10 devices:**
```bash
python3 main.py --mode burst --count 100
```

**2. High-load burst (1,000 events across 50 devices):**
```bash
python3 main.py --mode burst --count 1000 --devices 50
```

**3. Burst using real registered devices:**
```bash
python3 main.py --mode burst --count 500 --fetch-devices
```

---

## 3. Option B: Running with Docker Compose

The simulator is configured with the `simulator` profile in `docker-compose.yml`, so it does **not** run automatically on normal `docker compose up`.

From the project root directory (`leetJourny`):

### 3.1. Start the Simulator in the Background
```bash
docker compose --profile simulator up -d --build iot-simulator
```

### 3.2. View Live Simulator Logs
```bash
docker logs -f iot-simulator
```

### 3.3. Stop the Simulator
```bash
docker compose --profile simulator stop iot-simulator
```

*(To completely remove the container: `docker compose --profile simulator rm -f iot-simulator`)*

---

## 4. CLI Parameters Reference

You can customize the simulation by passing any of these arguments to `python3 main.py`:

| Parameter | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `--mode` | string | `stream` | Execution mode: `stream` or `burst` |
| `--devices` | int | `10` | Number of synthetic meter IDs to simulate (`meter-001` .. `meter-N`) |
| `--interval` | float | `3.0` | Pause in seconds between transmission cycles (used in `stream` mode) |
| `--count` | int | `100` | Total number of telemetry events to transmit (used in `burst` mode) |
| `--fetch-devices` | flag | `false` | When passed, queries `device-service` for registered device UUIDs |
| `--ingestion-url` | string | `http://localhost:8083/ingestion/energy` | Target Ingestion Service API endpoint |
| `--device-service-url`| string | `http://localhost:8082/devices` | Target Device Service API endpoint |

---

## 5. Environment Variables Reference

When running via Docker Compose, you can configure the simulator inside `docker-compose.yml` or your `.env` file:

```env
INGESTION_URL=http://ingestion-service:8083/ingestion/energy
DEVICE_SERVICE_URL=http://device-service:8082/devices
SIM_NUM_DEVICES=10
SIM_INTERVAL=3.0
SIM_BASE_LOAD=1.5
SIM_PEAK_MULTIPLIER=3.2
SIM_TIMEOUT=5.0
```

---

## 6. How to Verify It's Working

While the simulator is running, open another terminal and watch the events arrive in Kafka in real time:

```bash
docker exec -it kafka /opt/kafka/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic energy-usage-events
```
