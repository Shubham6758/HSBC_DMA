package com.coforge.hsbcdma.util;

import com.coforge.hsbcdma.dto.RolemgmtDTO.ModuleChildModuleDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class manages JWT token generation, validation and expiration
 * @author Vandana Pal
 */

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    // ------------------ Generate Token ------------------
    public String generateToken(String username, String roleName, List<ModuleChildModuleDTO> modules) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", roleName);
        claims.put("modules", modules);
//        claims.put("name",name);

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 5))
                .claims(claims)                // e.g., ["ADMIN", "USER"]
                .signWith(getSigningKey())
                .compact();
    }

    // ------------------ Extract Username ------------------
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // ------------------ Extract All Claims ------------------
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }



    // REFRESH TOKEN (7 days)
    public String generateRefreshToken(String userId) {
        return Jwts.builder()
                .subject(userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7))
                .signWith(getSigningKey())
                .compact();
    }


    // ------------------ Validate Token ------------------
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername());
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extract expiration as epoch seconds (for logout blacklist)
    public long getExpirationEpochSeconds(String token) {
        return getClaims(token).getExpiration().toInstant().getEpochSecond();
    }

}
 