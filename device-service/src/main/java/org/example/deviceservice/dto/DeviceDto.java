package org.example.deviceservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.deviceservice.entity.DeviceType;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDto {
    private UUID id;
    private String name;
    private DeviceType type;
    private String location;
    private UUID userId;
}
