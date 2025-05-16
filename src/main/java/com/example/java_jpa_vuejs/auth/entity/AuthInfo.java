package com.example.java_jpa_vuejs.auth.entity;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.java_jpa_vuejs.auth.AuthDto;
import com.example.java_jpa_vuejs.auth.JoinDto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Getter
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "auth_info")
public class AuthInfo{
	
    @Column(length = 10)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authMgtSn;

    @Column(length = 50)
    private String mobileNo;

    @Column(length = 50)
    private String mobileCo;

    @Column(length = 200)
    private String userDi;

    @Id
    @Column(length = 200)
    private String userCi;

    @Column(length = 100)
    private String userName;

    @Column(length = 10)
    private String gender;

    @Column(length = 100)
    private String resSeq;

    @Column(length = 8)
    private String birthdate;

    @Column(length = 10)
    private String nationalInfo;

    @Column(length = 20)
    private String authType;

    @Column(length = 20)
    private String authProvider;


    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @PrePersist
    public void onPrePersist() {
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
    }

    @PreUpdate
    public void onPreUpdate() {
        this.modifiedDate = LocalDateTime.now();
    }

	public AuthDto toDto(AuthInfo AuthInfoEntity) {

		AuthDto authDto = new AuthDto();

		authDto.setAuthMgtSn(AuthInfoEntity.authMgtSn);;
		authDto.setMobileNo(AuthInfoEntity.mobileNo);
		authDto.setMobileCo(AuthInfoEntity.mobileCo);
		authDto.setUserDi(AuthInfoEntity.userDi);
		authDto.setUserCi(AuthInfoEntity.userCi);
		authDto.setUserName(AuthInfoEntity.userName);
		authDto.setGender(AuthInfoEntity.gender);
		authDto.setResSeq(AuthInfoEntity.resSeq);
		authDto.setBirthdate(AuthInfoEntity.birthdate);
		authDto.setNationalInfo(AuthInfoEntity.nationalInfo);
		authDto.setAuthType(AuthInfoEntity.authType);
        authDto.setAuthProvider(AuthInfoEntity.authProvider);

		return authDto;
	}

    @Builder
    public AuthInfo(Long authMgtSn, String mobileNo, String mobileCo, String userDi, String userCi,
                    String userName, String gender, String resSeq, String birthdate, String nationalInfo,
                    String authType, String authProvider, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.authMgtSn = authMgtSn;
        this.mobileNo = mobileNo;
        this.mobileCo = mobileCo;
        this.userDi = userDi;
        this.userCi = userCi;
        this.userName = userName;
        this.gender = gender;
        this.resSeq = resSeq;
        this.birthdate = birthdate;
        this.nationalInfo = nationalInfo;
        this.authType = authType;
        this.authProvider = authProvider;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

	public AuthInfo(AuthInfo loginEntity) {}

}