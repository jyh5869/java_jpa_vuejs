package com.example.java_jpa_vuejs.auth;

import lombok.*;

import java.time.ZonedDateTime;

import com.example.java_jpa_vuejs.auth.entity.AuthInfo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
public class AuthDto {

    private Long authMgtSn;      // 요청 고유번호
    private String mobileNo;       // 휴대폰 번호
    private String mobileCo;       // 통신사
    private String userDi;         // 중복가입 확인값
    private String userCi;         // 연계정보
    private String userName;       // UTF8 이름
    private String gender;         // 성별 (1: 남성, 0: 여성)
    private String resSeq;         // 결과 고유번호
    private String birthdate;      // 생년월일 (yyyymmdd)
    private String nationalInfo;   // 내외국인 여부 (0: 내국인, 1: 외국인)
    private String authType;       // 인증수단 (e.g. M: 휴대폰)
    private String authProvider;   // 인증제공 업체

    public AuthInfo toEntity() {

        AuthInfo build = AuthInfo.builder()
                .authMgtSn(authMgtSn)
                .mobileNo(mobileNo)
                .mobileCo(mobileCo)
                .userDi(userDi)
                .userCi(userCi)
                .userName(userName)
                .gender(gender)
                .resSeq(resSeq)
                .birthdate(birthdate)
                .authProvider(authProvider)
                .nationalInfo(nationalInfo)
                .authType(authType)
                .build();

        return build;
    }
}