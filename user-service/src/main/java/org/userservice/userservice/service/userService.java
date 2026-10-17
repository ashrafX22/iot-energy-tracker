package org.userservice.userservice.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.userservice.userservice.dto.userDto;
import org.userservice.userservice.entity.User;
import org.userservice.userservice.repository.userRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class userService {

    private final userRepository userRepository;

    public userService(userRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public userDto createUser(userDto dto) {
        String effectiveUsername = dto.getUsername() != null ? dto.getUsername() : dto.getName();
        String rawPassword = dto.getPassword() != null ? dto.getPassword() : "default_password";

        User user = User.builder()
                .username(effectiveUsername)
                .email(dto.getEmail())
                .address(dto.getAddress())
                .alerting(dto.getAlerting() != null ? dto.getAlerting() : false)
                .energyAlertingThreshold(dto.getEnergyAlertingThreshold())
                .passwordHash(rawPassword) // Note: can be hashed with BCrypt
                .build();

        User savedUser = userRepository.save(user);

        return mapToDto(savedUser);
    }

    @Transactional(readOnly = true)
    public userDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToDto)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<userDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    public userDto updateUser(Long id,userDto userDto){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setAddress(userDto.getAddress());
        user.setAlerting(userDto.getAlerting());
        user.setEnergyAlertingThreshold(userDto.getEnergyAlertingThreshold());
        return mapToDto(userRepository.save(user));
    }
    public void deleteUser(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    private userDto mapToDto(User user) {
        return userDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getUsername())
                .email(user.getEmail())
                .address(user.getAddress())
                .alerting(user.getAlerting())
                .energyAlertingThreshold(user.getEnergyAlertingThreshold())
                .build();
    }


}
