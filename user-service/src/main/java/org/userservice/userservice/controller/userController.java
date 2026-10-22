package org.userservice.userservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.userservice.userservice.dto.userDto;
import org.userservice.userservice.service.userService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Slf4j
public class userController {
    private userService userService;

    public userController(userService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<userDto> createUser(@RequestBody userDto userDto) {
        userDto user = userService.createUser(userDto);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<List<userDto>> getAllUsers() {
        List<userDto> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<userDto> getUserById(@PathVariable UUID id) {
        userDto user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/address/{address}")
    public ResponseEntity<userDto> getUserByAddress(@PathVariable String address) {
        userDto user = userService.findByAddress(address);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<userDto> updateUser(@PathVariable UUID id, @RequestBody userDto userDto) {
        userDto user = userService.updateUser(id, userDto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
