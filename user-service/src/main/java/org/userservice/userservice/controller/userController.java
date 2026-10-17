package org.userservice.userservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.userservice.userservice.dto.userDto;
import org.userservice.userservice.service.userService;

import java.util.List;

@RestController
@RequestMapping("/users")
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
    public ResponseEntity<userDto> getUserById(@PathVariable Long id) {
        userDto user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<userDto> updateUser(@PathVariable Long id, @RequestBody userDto userDto) {
        userDto user = userService.updateUser(id, userDto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
