package ru.denusariy.demoapisecurity.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.denusariy.demoapisecurity.domain.dto.response.UserResponse;
import ru.denusariy.demoapisecurity.domain.entity.User;
import ru.denusariy.demoapisecurity.domain.enums.Role;
import ru.denusariy.demoapisecurity.exception.UserNotFoundException;
import ru.denusariy.demoapisecurity.repository.UserRepository;
import ru.denusariy.demoapisecurity.service.AdminService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findByRoleEquals(Role.ROLE_ADMIN).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse appointAdmin(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        user.setRole(Role.ROLE_ADMIN);
        userRepository.save(user);
        return convertToResponseDTO(user);
    }

    @Override
    public UserResponse removeAdmin(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);
        return convertToResponseDTO(user);
    }

    public UserResponse convertToResponseDTO(User user) {
        return modelMapper.map(user, UserResponse.class);
    }
}
