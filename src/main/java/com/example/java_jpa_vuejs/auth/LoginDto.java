package com.example.java_jpa_vuejs.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.example.java_jpa_vuejs.auth.entity.Members;

import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
public class LoginDto {

	private long id;

	@NotBlank(message = "'email' is a required input value")
	@Email(message = "It is not in email format")
	private String email;

	@NotBlank(message = "'password' is a required input value")
	private String password;

	private String token;
	
	public Members toEntity() {
		Members build = Members.builder()
				.id(id)
				.email(email)
				.password(password)
				.build();
		
		return build;
	}
	
}