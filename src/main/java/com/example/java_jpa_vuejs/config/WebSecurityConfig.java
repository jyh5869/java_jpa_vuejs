package com.example.java_jpa_vuejs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfiguration {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //http.authorizeHttpRequests().requestMatchers("/**").hasRole("USER");//.and().formLogin();
        //https://developer.okta.com/blog/2022/08/19/build-crud-spring-and-vue
        
        http
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) 
            .and()
            .httpBasic()
            .and()
            .authorizeRequests()
                .requestMatchers("/api/hello").permitAll()
                .requestMatchers("/**").authenticated()
                .anyRequest().authenticated() 
            .and()
            .csrf().disable();
        
        http.logout();
            //.logoutUrl("/api/logout") //logout url 설정
            //.logoutSuccessUrl("/api/login"); //logout 시 이동할 url만 설정
            // .addLogoutHandler(new LogoutHandler() { //logout이 성공했을 때 처리할 내용
            //     @Override
            //     public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
            //         HttpSession session = request.getSession();
            //         session.invalidate();
            //     }
            // })
            // .logoutSuccessHandler(new LogoutSuccessHandler() { //logout 시 이동할 url 및 더 많은 로직 구현 가능
            //     @Override
            //     public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            //         response.sendRedirect("/login"); //로그아웃 시 로그인 할 수 있는 페이지로 이동하도록 처리한다.
            //     }
            // })

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
            
        //auth.inMemoryAuthentication().withUser("user").password("1234").roles("USER");
            
        return new InMemoryUserDetailsManager(user);
    } 

    
        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
            return (web) -> web.ignoring().requestMatchers("/images/**", "/js/**", "/webjars/**");
        }
}
