package ru.denusariy.demoapisecurity.service;

import ru.denusariy.demoapisecurity.domain.dto.request.AdminRequest;
import ru.denusariy.demoapisecurity.domain.dto.response.UserResponse;

import java.util.List;

public interface AdminService {

    List<UserResponse> findAll();
    UserResponse updateAdmin(AdminRequest updatedAdmin);
    UserResponse appointAdmin(AdminRequest updatedAdmin);
    UserResponse removeAdmin(String email);
}
