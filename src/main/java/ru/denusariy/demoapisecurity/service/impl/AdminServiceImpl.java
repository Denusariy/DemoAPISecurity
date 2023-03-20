package ru.denusariy.demoapisecurity.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.denusariy.demoapisecurity.domain.dto.request.AdminRequest;
import ru.denusariy.demoapisecurity.domain.dto.response.UserResponse;
import ru.denusariy.demoapisecurity.domain.entity.User;
import ru.denusariy.demoapisecurity.domain.enums.Authority;
import ru.denusariy.demoapisecurity.repository.UserRepository;
import ru.denusariy.demoapisecurity.service.AdminService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findByAuthoritiesEquals(Authority.CREATE).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponse updateAdmin(AdminRequest updatedAdmin) {
        String email = updatedAdmin.getEmail();
        User adminToBeUpdated = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("Пользователь %s не найден!", email)));
        User admin = User.builder()
                .id(adminToBeUpdated.getId())
                .firstName(updatedAdmin.getFirstName())
                .lastName(updatedAdmin.getLastName())
                .email(email)
                .password(passwordEncoder.encode(updatedAdmin.getPassword()))
                .authorities(updatedAdmin.getAuthorities())
                .build();
        userRepository.save(admin);
        log.info("Обновлен админ " + email);
        return convertToResponseDTO(admin);
    }

    @Override
    @Transactional
    public UserResponse appointAdmin(AdminRequest updatedAdmin) {
        String email = updatedAdmin.getEmail();
        User adminToBeUpdated = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("Пользователь %s не найден!", email)));
        adminToBeUpdated.setAuthorities(updatedAdmin.getAuthorities());
        userRepository.save(adminToBeUpdated);
        log.info(String.format("Админу %s выданы новые права %s", email, updatedAdmin.getAuthorities().toString()));
        return convertToResponseDTO(adminToBeUpdated);
    }

    @Override
    @Transactional
    public UserResponse removeAdmin(String email) {
        User admin = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь %s не найден!", email)));
        admin.setAuthorities(new HashSet<>(Collections.singleton(Authority.BROWS)));
        userRepository.save(admin);
        log.info(String.format("Пользователь %s лишен прав админа", email));
        return convertToResponseDTO(admin);
    }

    public UserResponse convertToResponseDTO(User user) {
        return modelMapper.map(user, UserResponse.class);
    }
}
