package com.example.java_jpa_vuejs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.common.Util;
import com.example.java_jpa_vuejs.auth.repositoryJPA.MemberRepository;


@EnableJpaAuditing // Main method가 있는 클래스에 적용하여 JPA Auditing(감시, 감사) 기능을 활성화(@LastModifiedDate, @CreatedDate등을 사용하기 위함) 
@SpringBootApplication // (exclude = DataSourceAutoConfiguration.class) -> 데이터소스 자동 생성 제외 (Bean생성 안됨)
public class JavaJpaVuejsApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaJpaVuejsApplication.class, args);
	}
	
    private static final Logger LOG = LoggerFactory.getLogger(JavaJpaVuejsApplication.class);
    
    /**
     * JPA Test Bean
     */
    @Bean
    public CommandLineRunner demo(MemberRepository repository) throws InterruptedException {
    
        long reqTime = Util.durationTime ("start", "JPA 테스트", 0, "Proceeding" );
        
        return (args) -> {
            try {             

                repository.findByEmail("5869jyh@hanmail.net");
                Util.durationTime ("end", "JPA 테스트", reqTime, "Complete" );
            }
            catch (Exception e) {
                
                Util.durationTime ("end", "JPA 테스트", reqTime, "Fail" );
                e.printStackTrace();
            }
        };
    }
}
