package ru.denusariy.demoapisecurity.service;

import ru.denusariy.demoapisecurity.domain.dto.response.UserResponse;

import java.util.List;

public interface AdminService {

    List<UserResponse> findAll();
    UserResponse appointAdmin(String email);
    UserResponse removeAdmin(String email);
}
