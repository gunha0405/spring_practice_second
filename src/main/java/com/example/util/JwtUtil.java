package com.example.util;


import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.user.SiteUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	private final String secret = "abcdefghijklmnopqrstuvwxyz123456789";
	private final long expire = 1000 * 60 * 60;
	
	public String generateToken(SiteUser user) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expire);
        return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("tenant", user.getCustomerId())
            .claim("roles", List.of("USER"))
            .setIssuedAt(now).setExpiration(exp)
            .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
            .compact();
    }
	
	public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
            .build()
            .parseClaimsJws(token);
    }
}
