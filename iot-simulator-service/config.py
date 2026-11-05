import os
from dataclasses import dataclass


@dataclass
class SimulatorConfig:
    ingestion_url: str = os.getenv("INGESTION_URL", "http://localhost:8083/ingestion/energy")
    device_service_url: str = os.getenv("DEVICE_SERVICE_URL", "http://localhost:8082/devices")
    num_devices: int = int(os.getenv("SIM_NUM_DEVICES", "10"))
    interval_seconds: float = float(os.getenv("SIM_INTERVAL", "3.0"))
    base_load_kwh: float = float(os.getenv("SIM_BASE_LOAD", "1.5"))
    peak_multiplier: float = float(os.getenv("SIM_PEAK_MULTIPLIER", "3.2"))
    timeout_seconds: float = float(os.getenv("SIM_TIMEOUT", "5.0"))
