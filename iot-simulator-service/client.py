import json
import logging
import time
from typing import List, Dict, Any, Optional

try:
    import requests
    HAS_REQUESTS = True
except ImportError:
    HAS_REQUESTS = False
    import urllib.request
    import urllib.error

logger = logging.getLogger("iot-simulator")


class IngestionClient:
    """
    HTTP client for delivering telemetry to ingestion-service
    and optionally discovering registered devices from device-service.
    """

    def __init__(self, ingestion_url: str, device_service_url: str, timeout: float = 5.0):
        self.ingestion_url = ingestion_url
        self.device_service_url = device_service_url
        self.timeout = timeout
        if HAS_REQUESTS:
            self.session = requests.Session()
        else:
            self.session = None

    def send_reading(self, reading: Dict[str, Any]) -> bool:
        """
        Transmits a single reading payload to ingestion-service.
        Returns True if accepted (HTTP 202/200), False otherwise.
        """
        payload_bytes = json.dumps(reading).encode("utf-8")
        headers = {"Content-Type": "application/json"}

        start_time = time.perf_counter()
        try:
            if HAS_REQUESTS:
                resp = self.session.post(self.ingestion_url, data=payload_bytes, headers=headers, timeout=self.timeout)
                duration_ms = (time.perf_counter() - start_time) * 1000
                if resp.status_code in (200, 202):
                    logger.info(
                        f"[SENT] Meter: {reading['meterId']:<15} | Usage: {reading['energyUsage']:>6.2f} kWh | "
                        f"HTTP {resp.status_code} ({duration_ms:.1f}ms)"
                    )
                    return True
                else:
                    logger.warning(
                        f"[WARN] Meter: {reading['meterId']} | HTTP {resp.status_code}: {resp.text}"
                    )
                    return False
            else:
                req = urllib.request.Request(self.ingestion_url, data=payload_bytes, headers=headers, method="POST")
                with urllib.request.urlopen(req, timeout=self.timeout) as resp:
                    duration_ms = (time.perf_counter() - start_time) * 1000
                    status = resp.getcode()
                    if status in (200, 202):
                        logger.info(
                            f"[SENT] Meter: {reading['meterId']:<15} | Usage: {reading['energyUsage']:>6.2f} kWh | "
                            f"HTTP {status} ({duration_ms:.1f}ms)"
                        )
                        return True
                    else:
                        logger.warning(f"[WARN] Meter: {reading['meterId']} | HTTP {status}")
                        return False
        except Exception as e:
            logger.error(f"[FAIL] Could not send reading for {reading['meterId']}: {e}")
            return False

    def fetch_device_ids(self) -> Optional[List[str]]:
        """
        Attempts to fetch real registered device IDs from device-service.
        Returns list of UUID strings, or None if unavailable/empty.
        """
        logger.info(f"Attempting to fetch registered devices from: {self.device_service_url}")
        try:
            if HAS_REQUESTS:
                resp = self.session.get(self.device_service_url, timeout=self.timeout)
                if resp.status_code == 200:
                    data = resp.json()
                    ids = [d["id"] for d in data if "id" in d]
                    if ids:
                        logger.info(f"Discovered {len(ids)} registered devices from device-service.")
                        return ids
            else:
                req = urllib.request.Request(self.device_service_url, headers={"Accept": "application/json"})
                with urllib.request.urlopen(req, timeout=self.timeout) as resp:
                    if resp.getcode() == 200:
                        data = json.loads(resp.read().decode("utf-8"))
                        ids = [d["id"] for d in data if "id" in d]
                        if ids:
                            logger.info(f"Discovered {len(ids)} registered devices from device-service.")
                            return ids
        except Exception as e:
            logger.warning(f"Could not reach device-service ({e}). Falling back to synthetic meter IDs.")
        return None
