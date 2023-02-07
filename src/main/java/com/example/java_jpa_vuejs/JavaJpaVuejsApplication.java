package com.example.java_jpa_vuejs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.example.java_jpa_vuejs.auth.MemberRepository;


@SpringBootApplication //(exclude = DataSourceAutoConfiguration.class) -> 데이터소스 자동 생성 제외 (Bean생성 안됨)
public class JavaJpaVuejsApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaJpaVuejsApplication.class, args);
	}
	
    private static final Logger LOG = LoggerFactory.getLogger(JavaJpaVuejsApplication.class);
    
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner demo(MemberRepository repository) {
        
        return (args) -> {
            LOG.info("JPA 테스트 시작 - FALG = NONE");
            repository.findByEmail("5869jyh@hanmail.net");
            LOG.info("JPA 테스트 종료 - FLAG = SUCCESS");
        };
    }
}
