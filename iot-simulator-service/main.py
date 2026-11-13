import argparse
import logging
import signal
import sys
import time
import uuid
from typing import List

from config import SimulatorConfig
from generator import EnergyDataGenerator
from client import IngestionClient

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    datefmt="%H:%M:%S"
)
logger = logging.getLogger("iot-simulator")

running = True


def signal_handler(sig, frame):
    global running
    logger.info("\nGracefully shutting down IoT Simulator...")
    running = False


def generate_synthetic_device_ids(count: int) -> List[str]:
    return [str(uuid.uuid5(uuid.NAMESPACE_DNS, f"meter-{i:03d}")) for i in range(1, count + 1)]


def main():
    global running
    signal.signal(signal.SIGINT, signal_handler)
    signal.signal(signal.SIGTERM, signal_handler)

    default_cfg = SimulatorConfig()

    parser = argparse.ArgumentParser(description="IoT Smart Meter Telemetry Simulator for IotEnergyTracker")
    parser.add_argument(
        "--mode",
        choices=["stream", "burst"],
        default="stream",
        help="Simulation mode: 'stream' (continuous periodic) or 'burst' (rapid batch load)"
    )
    parser.add_argument(
        "--devices",
        type=int,
        default=default_cfg.num_devices,
        help=f"Number of synthetic devices to simulate (default: {default_cfg.num_devices})"
    )
    parser.add_argument(
        "--interval",
        type=float,
        default=default_cfg.interval_seconds,
        help=f"Transmission interval in seconds per device in stream mode (default: {default_cfg.interval_seconds}s)"
    )
    parser.add_argument(
        "--count",
        type=int,
        default=100,
        help="Total number of events to send in burst mode (default: 100)"
    )
    parser.add_argument(
        "--ingestion-url",
        type=str,
        default=default_cfg.ingestion_url,
        help=f"Target ingestion service URL (default: {default_cfg.ingestion_url})"
    )
    parser.add_argument(
        "--device-service-url",
        type=str,
        default=default_cfg.device_service_url,
        help=f"Target device-service URL (default: {default_cfg.device_service_url})"
    )
    parser.add_argument(
        "--fetch-devices",
        action="store_true",
        help="Query device-service for registered device UUIDs instead of using synthetic IDs"
    )

    args = parser.parse_args()

    client = IngestionClient(
        ingestion_url=args.ingestion_url,
        device_service_url=args.device_service_url,
        timeout=default_cfg.timeout_seconds
    )
    generator = EnergyDataGenerator(
        base_load=default_cfg.base_load_kwh,
        peak_multiplier=default_cfg.peak_multiplier
    )

    # Resolve device IDs
    device_ids = None
    if args.fetch_devices:
        device_ids = client.fetch_device_ids()

    if not device_ids:
        device_ids = generate_synthetic_device_ids(args.devices)
        logger.info(f"Using {len(device_ids)} synthetic meters: {device_ids[0]} ... {device_ids[-1]}")

    logger.info("=" * 65)
    logger.info("  IoT Device Simulator Initialized")
    logger.info(f"  Target Ingestion URL : {args.ingestion_url}")
    logger.info(f"  Active Devices       : {len(device_ids)}")
    logger.info(f"  Execution Mode       : {args.mode.upper()}")
    if args.mode == "stream":
        logger.info(f"  Stream Interval      : {args.interval}s")
    else:
        logger.info(f"  Burst Total Count    : {args.count}")
    logger.info("=" * 65)

    sent_count = 0
    fail_count = 0
    start_time = time.time()

    try:
        if args.mode == "burst":
            logger.info(f"Starting burst transmission of {args.count} events...")
            for i in range(args.count):
                if not running:
                    break
                meter_id = device_ids[i % len(device_ids)]
                reading = generator.generate_reading(meter_id)
                success = client.send_reading(reading)
                if success:
                    sent_count += 1
                else:
                    fail_count += 1

        elif args.mode == "stream":
            logger.info("Starting continuous telemetry stream (Press Ctrl+C to stop)...")
            while running:
                cycle_start = time.time()
                for meter_id in device_ids:
                    if not running:
                        break
                    reading = generator.generate_reading(meter_id)
                    success = client.send_reading(reading)
                    if success:
                        sent_count += 1
                    else:
                        fail_count += 1

                # Wait for remainder of the interval
                elapsed = time.time() - cycle_start
                sleep_time = max(0.0, args.interval - elapsed)
                if running and sleep_time > 0:
                    time.sleep(sleep_time)

    finally:
        total_time = max(0.001, time.time() - start_time)
        rate = sent_count / total_time
        logger.info("=" * 65)
        logger.info("  Simulation Summary")
        logger.info(f"  Total Duration   : {total_time:.2f}s")
        logger.info(f"  Successful Events: {sent_count}")
        logger.info(f"  Failed Events    : {fail_count}")
        logger.info(f"  Throughput Rate  : {rate:.1f} events/sec")
        logger.info("=" * 65)


if __name__ == "__main__":
    main()
