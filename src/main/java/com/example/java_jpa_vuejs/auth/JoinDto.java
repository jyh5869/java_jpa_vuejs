package com.example.java_jpa_vuejs.auth;

import lombok.*;

import java.time.ZonedDateTime;

import com.example.java_jpa_vuejs.auth.entity.Members;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
public class JoinDto {

    private long id;

    @Email(message = "do not email type")
    @NotBlank(message = "'email' is a required input value")
    private String email;

    @NotBlank(message = "'password' is a required input value")
    private String password;

    @NotBlank(message = "'name' is a required input value")
    private String name;

    @NotBlank(message = "'nickname' is a required input value")
    private String nickname;

    @NotBlank(message = "'mobile' is a required input value")
    private String mobile;

    private String address;

    @NotBlank(message = "'profile' is a required input value")
    private String profile;

    private ZonedDateTime createdDate;

    private ZonedDateTime modifiedDate;
    
    private String deleteYn;

    private String authType;

    private String token;

    private String actionType;

    private Boolean actionResCd;

    private String errorCd;

    private Integer actionCnt;
    
    public Object signFirebaseService;

    public String userCi;
    
    public Members toEntity() {

        Members build = Members.builder()
                .id(id)
                .email(email)
                .password(password)
                .name(name)
                .nickname(nickname)
                .mobile(mobile)
                .address(address)
                .profile(profile)
                .authType(authType)
                .deleteYn(deleteYn)
                .userCi(userCi)
                .build();

        return build;
    }
}