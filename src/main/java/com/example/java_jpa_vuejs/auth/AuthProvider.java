package com.example.java_jpa_vuejs.auth;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Base64;
import java.util.Date;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Component
public class AuthProvider {

    private static final Logger LOG = LoggerFactory.getLogger(AuthProvider.class);

    @Value("${spring.jwt.secret.signature}")
    private String atSecretKey;

    @PostConstruct
    protected void init() {
        atSecretKey = Base64.getEncoder().encodeToString(atSecretKey.getBytes());

    }

    // @Autowired
    // private UserDetailsService userDetailsService;

    /**
     * @throws Exception
     * @method 설명 : jwt 토큰 발급
     */
    public String createToken(long userPk, String email, String nickname) {
        
        LOG.info("Enter Create Token - Email :" + email + ", UserPk : " + userPk);
    	/**
    	 * 토큰발급을 위한 데이터는 UserDetails에서 담당
    	 * 따라서 UserDetails를 세부 구현한 CustomUserDetails로 회원정보 전달
    	 */
    	CustomUserDetails user = new CustomUserDetails(
    			userPk, 	// 번호
    			email,      // 이메일
                nickname);  // 닉네임
    	
    	// 유효기간설정을 위한 Date 객체 선언
    	Date date = new Date();
    
        final JwtBuilder builder = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject("accesstoken").setExpiration(new Date(date.getTime() + (1000L*60*60*12)))
                .claim("userPk", userPk)
                .claim("email", email)
                .claim("roles", user.getAuthorities())
                .signWith(SignatureAlgorithm.HS256, atSecretKey);

        LOG.info("End And Success Create Token - Email :" + email + ", UserPk : " + userPk);

        return builder.compact();
    }

     /**
     * @method 설명 : 토큰에서 회원정보 추출
     */
    public String getUserPk(String token) {

        return Jwts.parser().setSigningKey(atSecretKey).parseClaimsJws(token).getBody().getSubject();        
    }

    /**
     * @method 설명 : 컨텍스트에 해당 유저에 대한 권한을 전달하고 API 접근 전 접근 권한을 확인하여 접근 허용 또는 거부를 진행한다.
     */
    @SuppressWarnings("unchecked")
    public Authentication getAuthentication(String token) {
            // 토큰 기반으로 유저의 정보 파싱
    LOG.info("Start And Get Auth Info");
        Claims claims = Jwts.parser().setSigningKey(atSecretKey).parseClaimsJws(token).getBody();

        long userPk = claims.get("userPk", Integer.class);
        String email = claims.get("email", String.class);

        CustomUserDetails userDetails = new CustomUserDetails(userPk, email, email);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * @method 설명 : request객체 헤더에 담겨 있는 토큰 가져오기
     */
    public String resolveToken(HttpServletRequest request) {
        LOG.info("Start And Get Token");
        return request.getHeader("accesstoken");
    }

    /**
     * @method 설명 : 토큰 유효시간 만료여부 검사 실행
     */
    public boolean validateToken(String token) {
        try {
            LOG.info("Start And Check Token Validate - token :" + token);
            Jws<Claims> claims = Jwts.parser().setSigningKey(atSecretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } 
        catch (Exception e) {
            LOG.error("Error From Check Token Validate - token :" + token);
            return false;
        }
}
}