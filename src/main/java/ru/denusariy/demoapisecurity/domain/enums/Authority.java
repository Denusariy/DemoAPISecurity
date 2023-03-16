package ru.denusariy.demoapisecurity.domain.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {
    BROWS, CREATE, UPDATE, DELETE, SUPER_ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
