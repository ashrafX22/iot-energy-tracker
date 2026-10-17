package org.userservice.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.userservice.userservice.entity.User;

import java.util.Optional;

@Repository
public interface userRepository extends JpaRepository<User, Long> {
}
