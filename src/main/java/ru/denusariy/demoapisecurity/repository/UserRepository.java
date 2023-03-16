package ru.denusariy.demoapisecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.denusariy.demoapisecurity.domain.entity.User;
import ru.denusariy.demoapisecurity.domain.enums.Authority;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    List<User> findByAuthoritiesEquals(Authority authority);
}
