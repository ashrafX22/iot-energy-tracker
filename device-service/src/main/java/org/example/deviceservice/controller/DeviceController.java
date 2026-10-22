package org.example.deviceservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.deviceservice.dto.DeviceDto;
import org.example.deviceservice.service.DeviceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/devices")
@Slf4j
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping
    public ResponseEntity<DeviceDto> createDevice(@RequestBody DeviceDto deviceDto) {
        DeviceDto created = deviceService.createDevice(deviceDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DeviceDto>> getAllDevices() {
        List<DeviceDto> devices = deviceService.getAllDevices();
        return new ResponseEntity<>(devices, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceDto> getDeviceById(@PathVariable UUID id) {
        DeviceDto device = deviceService.getDeviceById(id);
        return new ResponseEntity<>(device, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DeviceDto>> getDevicesByUserId(@PathVariable UUID userId) {
        List<DeviceDto> devices = deviceService.getDevicesByUserId(userId);
        return new ResponseEntity<>(devices, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceDto> updateDevice(@PathVariable UUID id, @RequestBody DeviceDto deviceDto) {
        DeviceDto updated = deviceService.updateDevice(id, deviceDto);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable UUID id) {
        deviceService.deleteDevice(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
