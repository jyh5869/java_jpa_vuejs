package com.example.java_jpa_vuejs.auth;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
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
import java.time.Duration;

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

    private UserDetailsService userDetailsService;

    //private long accessTokenValidTime = Duration.ofSeconds(10).toMillis(); // 만료시간 10분인 엑세스 토큰 
    private long accessTokenValidTime = Duration.ofMinutes(60).toMillis(); // 만료시간 10분인 엑세스 토큰 
    private long refreshTokenValidTime = Duration.ofDays(1).toMillis(); // 만료시간 하루 리프레쉬 토큰
    //private long refreshTokenValidTime = Duration.ofSeconds(20).toMillis(); // 만료시간 10분인 엑세스 토큰 
    

    /**
     * @throws Exception
     * @method 설명 : jwt 토큰 발급()
     */
    public String createTokenCustom(long tokenValidTime, String id) {

        LOG.info("Enter Create Token - Email : " + id + " / validTime : " + tokenValidTime);
    	
    	// 유효기간설정을 위한 Date 객체 선언
    	Date now = new Date();
    
        final JwtBuilder builder = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject("accesstoken")
                .setExpiration(new Date(now.getTime() + accessTokenValidTime))
                .claim("email", id)
                .signWith(SignatureAlgorithm.HS256, atSecretKey);

        LOG.info("End And Success Create Token - Email :" + id + " / validTime : " + tokenValidTime);

        return builder.compact();
    }


    /**
     * @throws Exception
     * @method 설명 : jwt 토큰 발급()
     */
    public String createToken(AuthenticationDto authentication) {
        
        long userPk  = authentication.getId();
        String userEmail = authentication.getEmail();
        String userNickname = authentication.getNickname();

        LOG.info("Enter Create Token - Email :" + userEmail + ", UserPk : " + userPk + ", userNickname : " + userNickname);

    	/**
    	 * 토큰발급을 위한 데이터는 UserDetails에서 담당
    	 * 따라서 UserDetails를 세부 구현한 CustomUserDetails로 회원정보 전달
    	 */
    	CustomUserDetails user = new CustomUserDetails(
    			userPk, 	    // 번호
    			userEmail,      // 이메일
                userNickname);  // 닉네임
    	
    	// 유효기간설정을 위한 Date 객체 선언
    	Date now = new Date();
    
        final JwtBuilder builder = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject("accesstoken")
                .setExpiration(new Date(now.getTime() + accessTokenValidTime))
                .claim("userPk", userPk)
                .claim("email", userEmail)
                .claim("roles", user.getAuthorities())
                .signWith(SignatureAlgorithm.HS256, atSecretKey);

        LOG.info("End And Success Create Token - Email :" + userEmail + ", UserPk : " + userPk);

        return builder.compact();
    }

    public String createRefreshToken(AuthenticationDto authentication) {

        long userPk  = authentication.getId();
        String userEmail = authentication.getEmail();
        String userNickname = authentication.getNickname();

        LOG.info("Enter Create Token - Email :" + userEmail + ", UserPk : " + userPk + ", userNickname : " + userNickname);
    	/**
    	 * 토큰발급을 위한 데이터는 UserDetails에서 담당
    	 * 따라서 UserDetails를 세부 구현한 CustomUserDetails로 회원정보 전달
    	 */
    	CustomUserDetails user = new CustomUserDetails(
    			userPk, 	    // 번호
    			userEmail,      // 이메일
                userNickname);  // 닉네임

        Date now = new Date();

        final JwtBuilder builder = Jwts.builder()
                .setIssuedAt(now)
                .setHeaderParam("typ", "JWT")
                .setSubject("refreshtoken")
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
                .claim("userPk", userPk)
                .claim("email", userEmail)
                .claim("roles", user.getAuthorities())
                .signWith(SignatureAlgorithm.HS256, atSecretKey);
        
        LOG.info("End And Success Create RefreshToken - Email :" + userEmail + ", UserPk : " + userPk);

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
    public String resolveToken(HttpServletRequest request, String tokenType) {
        LOG.info("Start And Get Token");

        if(tokenType.equals("accesstoken")){
            return request.getHeader("accesstoken");
        }
        else if(tokenType.equals("refreshtoken")){
            return request.getHeader("refreshtoken");
        }    
        else{
            return "Token Empty";
        }
    }

    /**
     * @method 설명 : 토큰 유효시간 만료여부 검사 실행
     */
    public boolean validateToken(String token) {
        try {
            LOG.info("Start And Check Token Validate - token :" + token);
            Jws<Claims> claims = Jwts.parser().setSigningKey(atSecretKey).parseClaimsJws(token);
            System.out.println(claims.getBody().getExpiration().before(new Date()));
            return !claims.getBody().getExpiration().before(new Date());
        } 
        catch (Exception e) {
            LOG.error("Error From Check Token Validate - token :" + token);
            return false;
        }
    }
    
}