import datetime
import unittest
import sys
import os

# Ensure iot-simulator package is in path
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

from generator import EnergyDataGenerator


class TestEnergyDataGenerator(unittest.TestCase):

    def setUp(self):
        self.generator = EnergyDataGenerator(base_load=2.0, peak_multiplier=3.0)

    def test_reading_structure(self):
        reading = self.generator.generate_reading("meter-042")
        self.assertIn("meterId", reading)
        self.assertIn("timestamp", reading)
        self.assertIn("energyUsage", reading)
        self.assertEqual(reading["meterId"], "meter-042")
        self.assertIsInstance(reading["energyUsage"], float)
        self.assertGreater(reading["energyUsage"], 0.0)

    def test_timestamp_iso_format(self):
        fixed_dt = datetime.datetime(2026, 10, 29, 14, 30, 0, tzinfo=datetime.timezone.utc)
        reading = self.generator.generate_reading("meter-001", timestamp=fixed_dt)
        self.assertEqual(reading["timestamp"], "2026-10-29T14:30:00Z")

    def test_diurnal_peak_higher_than_night(self):
        night_factor = self.generator.calculate_diurnal_factor(3.0)   # 3 AM
        evening_factor = self.generator.calculate_diurnal_factor(19.5) # 7:30 PM
        self.assertGreater(evening_factor, night_factor)


if __name__ == "__main__":
    unittest.main()
