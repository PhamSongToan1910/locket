package com.example.locket_clone.config.security;

import com.example.locket_clone.entities.User;
import com.example.locket_clone.repository.InterfacePackage.UserRepository;
import com.example.locket_clone.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private UserService userService;

    @PostConstruct
    protected void init(){
        this.tokenSecretKey = Base64.getEncoder().encodeToString(tokenSecretKey.getBytes());
        this.tokenValidityInMilliseconds = 1000 * tokenValidityInSeconds;

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
        Date validity = new Date(now.getTime() + refreshTokenValidityInSeconds);
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
        User user = userService.findUserById(userId);
        Set<SimpleGrantedAuthority> simpleGrantedAuthorities = authorities.stream().map(authority -> (SimpleGrantedAuthority) authority).collect(Collectors.toSet());
        CustomUserDetail principal = new CustomUserDetail(user, simpleGrantedAuthorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public String getUserIdByToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(tokenSecretKey)
                .parseClaimsJws(token)
                .getBody();

        return claims.get(USER_ID_KEY).toString();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(tokenSecretKey)
                    .parseClaimsJws(token);
            return true;
        } catch (IllegalArgumentException e) {
            log.error("Token validation error {}", e.getMessage());
        }
        return false;
    }
}
