
package com.example.java_jpa_vuejs.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.java_jpa_vuejs.auth.AuthProvider;
import com.example.java_jpa_vuejs.auth.point.CustomAccessDeniedPoint;
import com.example.java_jpa_vuejs.auth.point.CustomAuthenticationEntryPoint;
import com.example.java_jpa_vuejs.auth.point.CustomFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
//https://www.baeldung.com/spring-security-custom-logout-handler
//https://velog.io/@shinhyocheol/%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EA%B8%B0%EB%8A%A51
//https://velog.io/@shinhyocheol/%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EA%B8%B0%EB%8A%A52
//https://github.com/shinhyocheol/board-api-example/blob/master/src/main/java/kr/co/platform/util/auth/point/CustomAuthenticationEntryPoint.java
//@Configuration
//@RequiredArgsConstructor


@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig extends WebSecurityConfiguration {

    private final AuthProvider authProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("http 객체 생성-------------------------------s");
        http
			.httpBasic().disable()
				.csrf().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
			.authorizeRequests()
				//.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
					.requestMatchers("/signup").permitAll()			// 회원가입
					.requestMatchers("/api/signin").permitAll() 		// 로그인
					.requestMatchers("/exception/**").permitAll() 	// 예외처리 포인트
					//.requestMatchers("/**").hasRole("USER")				// 이외 나머지는 USER 권한필요
                    .anyRequest().authenticated() 
				.and()
			.cors()
				.and()
			.exceptionHandling().accessDeniedHandler(new CustomAccessDeniedPoint())
				.and()
			.exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
				.and()
			.addFilterBefore(new CustomFilter(authProvider), UsernamePasswordAuthenticationFilter.class);
            System.out.println("http 객체 생성-------------------------------e");
        return http.build();
    }

    
    @Bean
    public UserDetailsService userDetailsService() throws Exception {
        System.out.println("하위하위하위하위");  
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        
        UserDetails user = User
            .withUsername("user")
            .password(encoder.encode("1234"))
            .roles("USER")
            .build();
        System.out.println(user.getPassword());
            
        return new InMemoryUserDetailsManager(user);
    } 
    

    // @Bean
    // public UserDetailsService userDetailsService() {
    //     return userDetailsService();
    // }
     
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
    

    
    // @Bean
	// public AuthenticationManager authenticationManager() throws Exception {
	// 	return authenticationManager();
	// } 
        /* 
    @Bean
    public AuthenticationManager authenticationManagerBeanManager() throws Exception {
        return authenticationManagerBeanManager();
    }
    */

    

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/images/**", "/js/**", "/webjars/**");
    }
}
