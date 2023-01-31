package com.example.java_jpa_vuejs;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.example.java_jpa_vuejs.auth.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactory")

//@EnableJpaRepositories(basePackages = {"com.example.java_jpa_vuejs.auth"}) // com.my.jpa.repository 하위에 있는 jpaRepository를 상속한 repository scan
//@EntityScan(basePackages = {"com.example.java_jpa_vuejs.auth"}) // com.my.jpa.entity 하위에 있는 @Entity 클래스 scan
//@ComponentScan(basePackages = {"com.example.java_jpa_vuejs.auth"})
@SpringBootApplication //(exclude = DataSourceAutoConfiguration.class)
public class JavaJpaVuejsApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaJpaVuejsApplication.class, args);
	}
	
    //private static final Logger log = LoggerFactory.getLogger(AccessingDataJpaApplication.class);
    
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public CommandLineRunner demo(MemberRepository repository) {
        return (args) -> {

        System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
        repository.findByEmail("5869jyh@hanmail.net");
        System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
        /* 
        // save a few customers
        repository.save(new Customer("Jack", "Bauer"));
        repository.save(new Customer("Chloe", "O'Brian"));
        repository.save(new Customer("Kim", "Bauer"));
        repository.save(new Customer("David", "Palmer"));
        repository.save(new Customer("Michelle", "Dessler"));

        // fetch all customers
        log.info("Customers found with findAll():");
        log.info("-------------------------------");
        for (Customer customer : repository.findAll()) {
            log.info(customer.toString());
        }
        log.info("");

        // fetch an individual customer by ID
        Customer customer = repository.findById(1L);
        log.info("Customer found with findById(1L):");
        log.info("--------------------------------");
        log.info(customer.toString());
        log.info("");

        // fetch customers by last name
        log.info("Customer found with findByLastName('Bauer'):");
        log.info("--------------------------------------------");
        repository.findByLastName("Bauer").forEach(bauer -> {
            log.info(bauer.toString());
        });
        // for (Customer bauer : repository.findByLastName("Bauer")) {
        //  log.info(bauer.toString());
        // }
        log.info("");
        */
        };
        
    }
    
    /* 
	// Fix the CORS errors
    @Bean
    public FilterRegistrationBean simpleCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        //config.setAllowCredentials(true);
        // *** URL below needs to match the Vue client URL and port ***
        config.setAllowedOrigins(Collections.singletonList("*"));
        config.setAllowedMethods(Collections.singletonList("*"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
    */
}
