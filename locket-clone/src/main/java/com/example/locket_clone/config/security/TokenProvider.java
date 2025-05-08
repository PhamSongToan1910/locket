package com.example.locket_clone.config.security;

import com.example.locket_clone.entities.User;
import com.example.locket_clone.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenProvider {

    @Value("${security.jwt.secret}")
    String tokenSecretKey;
    @Value("${security.jwt.token-validity-in-seconds}")
    Long tokenValidityInSeconds;
    @Value("${security.jwt.refresh-token-validity-in-seconds}")
    Long refreshTokenValidityInSeconds;

    private static final String AUTHORITIES_KEY = "auth";
    private static final String USER_ID_KEY = "userId";

    long tokenValidityInMilliseconds;
    long refreshTokenValidityInMilliseconds;

    @PostConstruct
    protected void init(){
        this.tokenSecretKey = Base64.getEncoder().encodeToString(tokenSecretKey.getBytes());
        this.tokenValidityInMilliseconds = 1000 * tokenValidityInSeconds;
        this.refreshTokenValidityInMilliseconds = 1000 * refreshTokenValidityInSeconds;
    }

    public String createToken(Authentication authentication, String userId) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        Claims claims = Jwts.claims().setSubject(authentication.getName());
        claims.put(AUTHORITIES_KEY, authorities);
        claims.put(USER_ID_KEY, userId);
        //todo put another claims

        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS512, tokenSecretKey)
                .compact();
    }

    public String createRefreshToken(Authentication authentication, String userId) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        Claims claims = Jwts.claims().setSubject(authentication.getName());
        claims.put(AUTHORITIES_KEY, authorities);
        claims.put(USER_ID_KEY, userId);
        //todo put another claims

        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidityInMilliseconds);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS512, tokenSecretKey)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(tokenSecretKey)
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities = Arrays
                .stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .filter(auth -> !auth.trim().isEmpty())
                .map(SimpleGrantedAuthority::new)
                .toList();

        String userId = claims.get(USER_ID_KEY).toString();
        Set<SimpleGrantedAuthority> simpleGrantedAuthorities = authorities.stream().map(authority -> (SimpleGrantedAuthority) authority).collect(Collectors.toSet());
        CustomUserDetail principal = new CustomUserDetail(userId, simpleGrantedAuthorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public String getUserIdByToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(tokenSecretKey)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get(USER_ID_KEY).toString();
        } catch (ExpiredJwtException e) {
            claims = e.getClaims();
            if (claims != null && claims.containsKey(USER_ID_KEY)) {
                return claims.get(USER_ID_KEY).toString();
            } else {
                log.error("Không thể lấy userId từ token đã hết hạn hoặc key không tồn tại.");
                return null;
            }
        } catch (Exception e) {
            // Xử lý các loại exception khác (ví dụ: MalformedJwtException, SignatureException,...)
            log.error("Lỗi parse token: " + e.getMessage());
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(tokenSecretKey)
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Token đã hết hạn: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Token không hợp lệ: {}", e.getMessage());
        } catch (SignatureException e) {
            log.error("Chữ ký JWT không hợp lệ: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Token validation error: {}", e.getMessage());
        }
        return false;
    }
}
