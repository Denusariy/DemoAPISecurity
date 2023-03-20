package ru.denusariy.demoapisecurity.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.denusariy.demoapisecurity.domain.dto.request.AdminRequest;
import ru.denusariy.demoapisecurity.domain.dto.response.UserResponse;
import ru.denusariy.demoapisecurity.service.AdminService;

import java.util.List;

@Controller
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping()
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(adminService.findAll());
    }

    @PostMapping("/appoint")
    public ResponseEntity<UserResponse> appoint(@RequestBody AdminRequest updatedAdmin) {
        return ResponseEntity.ok(adminService.appointAdmin(updatedAdmin));
    }

    @PostMapping("/update")
    public ResponseEntity<UserResponse> update(@RequestBody AdminRequest updatedAdmin) {
        return ResponseEntity.ok(adminService.updateAdmin(updatedAdmin));
    }

    @PostMapping("/remove")
    public ResponseEntity<UserResponse> remove(@RequestBody String email) {
        return ResponseEntity.ok(adminService.removeAdmin(email));
    }

}
