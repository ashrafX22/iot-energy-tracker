package org.example.usageservice.client.device.dto;

import java.util.UUID;

/**
 * DTO representing the device data contract returned by device-service.
 */
public record DeviceResponseDto(
    UUID id,
    String name,
    String type,
    String location,
    UUID userId
) {}
