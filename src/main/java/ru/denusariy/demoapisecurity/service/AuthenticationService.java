package ru.denusariy.demoapisecurity.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.denusariy.demoapisecurity.config.JWTService;
import ru.denusariy.demoapisecurity.domain.dto.request.AuthenticationRequest;
import ru.denusariy.demoapisecurity.domain.dto.request.RegisterRequest;
import ru.denusariy.demoapisecurity.domain.dto.response.AuthenticationResponse;
import ru.denusariy.demoapisecurity.domain.entity.User;
import ru.denusariy.demoapisecurity.domain.enums.Authority;
import ru.denusariy.demoapisecurity.repository.UserRepository;

import java.util.Set;

@Service
@Log4j2
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .authorities(Set.of(Authority.BROWS))
                .build();
        repository.save(user);
        log.info("Зарегистрирован пользователь " + user.getEmail());
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword()
        ));
        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        log.info("Аутентифицирован пользователь " + user.getEmail());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
