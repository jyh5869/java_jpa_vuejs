package com.example.java_jpa_vuejs.auth;

import com.example.java_jpa_vuejs.validation.Members;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
public class LoginDto {

	@NotBlank(message = "'email' is a required input value")
	@Email(message = "It is not in email format")
	private String email;

	@NotBlank(message = "'password' is a required input value")
	private String password;

	
	public Members toEntity() {
		Members build = Members.builder()
				.email(email)
				.password(password)
				.build();
		
		return build;
	}
	
}