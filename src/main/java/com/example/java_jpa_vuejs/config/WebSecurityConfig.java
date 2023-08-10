
package com.example.java_jpa_vuejs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.example.java_jpa_vuejs.auth.AuthProvider;
import com.example.java_jpa_vuejs.auth.JwtExceptionFilter;
import com.example.java_jpa_vuejs.auth.point.CustomAccessDeniedPoint;
import com.example.java_jpa_vuejs.auth.point.CustomAuthenticationEntryPoint;
import com.example.java_jpa_vuejs.auth.point.CustomFilter;

import lombok.RequiredArgsConstructor;


/**
 * 스프링 시큐리티 설정 파일
 * @see https://velog.io/@shinhyocheol/%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EA%B8%B0%EB%8A%A51
 * @see https://www.baeldung.com/spring-security-custom-logout-handler
 */
@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig extends WebSecurityConfiguration {
    
    private static final Logger LOG = LoggerFactory.getLogger(WebSecurityConfig.class);

    private final AuthProvider authProvider;
    private final JwtExceptionFilter jwtExceptionFilter;
    

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        LOG.info("스프링시큐리티 http 객체 생성 START");
        
        CustomFilter customFilter =  new CustomFilter(authProvider);

        http
			.httpBasic().disable()
			.csrf().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
			.authorizeRequests()
				    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll() // CORS 정보 사전 전달 에대한 접근
					.requestMatchers("/api/signup").permitAll()// 회원가입 페이지 접근
                    .requestMatchers("/api/signin").permitAll()// 로그인 페이지 접근 
                    .requestMatchers("/api/userRegistration").permitAll()// 회원가입 페이지 접근
					.requestMatchers("/api/idValidation").permitAll()// 로그인 페이지 접근
                    .requestMatchers("/api/reissuance").permitAll()// 토큰 재발행
                    .requestMatchers("/api/setSendAuthEmail").permitAll()// 토큰 재발행 
                    .requestMatchers("/api/getUserInfoAuthEmail").permitAll()// 토큰 재발행
					.requestMatchers("/exception/**").permitAll() // 예외처리 포인트 접근
					//.requestMatchers("/**").hasRole("USER")// 상세 권한 설정
                    .anyRequest().authenticated() 
				.and()
			.cors()
				.and()
			.exceptionHandling().accessDeniedHandler(new CustomAccessDeniedPoint())
				.and()
			.exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
			    .and()
			.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
            //.addFilterBefore(jwtExceptionFilter, CustomFilter.class);

        LOG.info("스프링시큐리티 http 객체 생성 END");

        return http.build();
    }

    /**
     * 스프링시큐리티 테스트 설정 빈
     * @param null
     * @return 스프링 시큐리티 테스트 USER 정보
     */    
    @Bean
    public UserDetailsService userDetailsService() throws Exception {
 
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        
        UserDetails user = User
            .withUsername("user")
            .password(encoder.encode("1234"))
            .roles("USER")
            .build();
            
        return new InMemoryUserDetailsManager(user);
    } 
    
    /**
     * 크로스 도메인 설정
     * @param null
     * @return 크로스도메인 설정 객체
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
            
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setMaxAge((long) 3600);
        configuration.setAllowCredentials(false);
        configuration.addExposedHeader("accessToken");
        configuration.addExposedHeader("content-disposition");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
    
    /**
     * 스프링 시큐리티 필터 예외 확장자
     * @param null
     * @return 제외할 확장자
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/images/**", "/js/**", "/webjars/**");
    }
}
