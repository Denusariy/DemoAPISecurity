package ru.denusariy.demoapisecurity.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.denusariy.demoapisecurity.domain.enums.Authority;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Set<Authority> authorities;
}
