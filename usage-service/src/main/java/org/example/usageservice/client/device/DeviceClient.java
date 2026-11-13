package org.example.usageservice.client.device;

import org.example.usageservice.client.device.dto.DeviceResponseDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;
import java.util.UUID;

@HttpExchange("/devices")
public interface DeviceClient {

    @GetExchange("/user/{userId}")
    List<DeviceResponseDto> getDevicesByUserId(@PathVariable("userId") UUID userId);

    @GetExchange("/{id}")
    DeviceResponseDto getDeviceById(@PathVariable("id") UUID id);
}