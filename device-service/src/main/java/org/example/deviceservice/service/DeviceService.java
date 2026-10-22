package org.example.deviceservice.service;

import org.example.deviceservice.dto.DeviceDto;
import org.example.deviceservice.entity.Device;
import org.example.deviceservice.exception.DeviceNotFoundException;
import org.example.deviceservice.repository.DeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Transactional
    public DeviceDto createDevice(DeviceDto dto) {
        Device device = Device.builder()
                .name(dto.getName())
                .type(dto.getType())
                .location(dto.getLocation())
                .userId(dto.getUserId())
                .build();

        Device savedDevice = deviceRepository.save(device);
        return mapToDto(savedDevice);
    }

    @Transactional(readOnly = true)
    public DeviceDto getDeviceById(UUID id) {
        return deviceRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<DeviceDto> getAllDevices() {
        return deviceRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DeviceDto> getDevicesByUserId(UUID userId) {
        return deviceRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public DeviceDto updateDevice(UUID id, DeviceDto dto) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with id: " + id));

        device.setName(dto.getName());
        device.setType(dto.getType());
        device.setLocation(dto.getLocation());
        device.setUserId(dto.getUserId());

        return mapToDto(deviceRepository.save(device));
    }

    @Transactional
    public void deleteDevice(UUID id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with id: " + id));
        deviceRepository.delete(device);
    }

    private DeviceDto mapToDto(Device device) {
        return DeviceDto.builder()
                .id(device.getId())
                .name(device.getName())
                .type(device.getType())
                .location(device.getLocation())
                .userId(device.getUserId())
                .build();
    }
}
