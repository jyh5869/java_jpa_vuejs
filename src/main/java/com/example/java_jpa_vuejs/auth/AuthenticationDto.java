package com.example.java_jpa_vuejs.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationDto {
    
    @Min(0)
    private Long id;

    @Email
    private String email;

    private String name;

    private String nickname;

    private String mobile;

    private String createdDate;

    private String modifiedDate;

    private String deleteYn;

    private String authType;

    private String loginType;

}
