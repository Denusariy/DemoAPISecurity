package ru.denusariy.demoapisecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.denusariy.demoapisecurity.domain.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
}
