import datetime
import math
import random
from typing import Dict, Any


class EnergyDataGenerator:
    """
    Generates realistic energy consumption telemetry for smart meters.
    Simulates base load, diurnal circadian patterns (morning/evening peaks),
    Gaussian micro-variations, and random appliance spikes.
    """

    def __init__(self, base_load: float = 1.5, peak_multiplier: float = 3.2):
        self.base_load = base_load
        self.peak_multiplier = peak_multiplier

    def calculate_diurnal_factor(self, hour: float) -> float:
        """
        Calculates a multiplier based on the hour of the day (0.0 to 24.0).
        Models morning peak (~07:30) and evening peak (~19:30).
        """
        # Morning peak around 7:30 AM (Gaussian bell)
        morning_peak = math.exp(-((hour - 7.5) ** 2) / 3.0)
        # Evening peak around 7:30 PM (Gaussian bell)
        evening_peak = math.exp(-((hour - 19.5) ** 2) / 4.5)

        factor = 1.0 + (self.peak_multiplier - 1.0) * (0.65 * morning_peak + 0.95 * evening_peak)
        # Late-night drop (01:00 - 05:00)
        if 1.0 <= hour <= 5.0:
            factor *= 0.6
        return factor

    def generate_reading(self, meter_id: str, timestamp: datetime.datetime = None) -> Dict[str, Any]:
        """
        Generates a single energy usage reading payload matching the ingestion service DTO:
        {
            "meterId": str,
            "timestamp": str (ISO 8601 UTC),
            "energyUsage": float
        }
        """
        if timestamp is None:
            timestamp = datetime.datetime.now(datetime.timezone.utc)

        hour = timestamp.hour + timestamp.minute / 60.0 + timestamp.second / 3600.0
        diurnal_factor = self.calculate_diurnal_factor(hour)

        # Baseline consumption for this hour
        current_load = self.base_load * diurnal_factor

        # Add Gaussian noise (±10%)
        noise = random.gauss(0, 0.1 * current_load)
        current_load += noise

        # 3% chance of high-power appliance spike (EV charger, HVAC, dryer: +3.0 to +8.0 kWh)
        if random.random() < 0.03:
            current_load += random.uniform(3.0, 8.0)

        # Ensure consumption never drops below minimum idle consumption (0.2 kWh)
        final_energy = max(0.2, round(current_load, 2))

        # Format ISO timestamp ending with 'Z'
        iso_timestamp = timestamp.strftime("%Y-%m-%dT%H:%M:%SZ")

        return {
            "meterId": str(meter_id),
            "timestamp": iso_timestamp,
            "energyUsage": final_energy
        }
