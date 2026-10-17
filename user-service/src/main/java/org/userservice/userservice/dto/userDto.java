package org.userservice.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class userDto {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String address;
    private Boolean alerting;
    private Double energyAlertingThreshold;
    private String password;

    public String getUsername() {
        return username != null ? username : name;
    }
}
