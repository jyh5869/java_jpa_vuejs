package com.example.java_jpa_vuejs.auth;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
public class JoinDto {

    @NotBlank(message = "'email' is a required input value")
    @Email(message = "do not email type")
    private String email;

    @NotBlank(message = "'password' is a required input value")
    private String password;

    @NotBlank(message = "'name' is a required input value")
    private String name;

    @NotBlank(message = "'nickname' is a required input value")
    private String nickname;

    @NotBlank(message = "'mobile' is a required input value")
    private String mobile;

    @NotBlank(message = "'profile' is a required input value")
    private String profile;

    public Members toEntity() {

        Members build = Members.builder()
                .email(email)
                .password(password)
                .name(name)
                .nickname(nickname)
                .mobile(mobile)
                .build();

        return build;
    }

}