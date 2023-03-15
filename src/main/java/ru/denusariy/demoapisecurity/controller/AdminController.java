package ru.denusariy.demoapisecurity.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<UserResponse> appoint(@RequestBody String email) {
        return ResponseEntity.ok(adminService.appointAdmin(email));
    }

    @PostMapping("/remove")
    public ResponseEntity<UserResponse> remove(@RequestBody String email) {
        return ResponseEntity.ok(adminService.removeAdmin(email));
    }

}
