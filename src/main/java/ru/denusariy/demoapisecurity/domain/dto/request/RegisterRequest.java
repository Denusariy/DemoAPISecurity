package ru.denusariy.demoapisecurity.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @JsonSetter(value = "first_name")
    private String firstName;
    @JsonSetter(value = "last_name")
    private String lastName;
    private String email;
    private String password;
}
