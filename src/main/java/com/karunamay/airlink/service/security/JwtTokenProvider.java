package com.karunamay.airlink.security;

import com.karunamay.airlink.exceptions.JwtAuthenticationException;
import com.karunamay.airlink.exceptions.TokenExpiredException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    @Value("${app.jwt.accessTokenExpirationInSec}")
    private String accessTokenExpirationInSec;

    @Value("${app.jwt.refreshTokenExpirationInSec}")
    private String refreshTokenExpirationInSec;

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.audience}")
    private String audience;

    @Value("${app.jwt.privateKeySystemPath}")
    private String privateKeySystemPath;

    @Value("${app.jwt.publicKeySystemPath}")
    private String publicKeySystemPath;

    public String generateAccessToken(String username) {

        Date iat = new Date();
        long accessTokenExpirationInMs = Long.valueOf(accessTokenExpirationInSec) * 1000L;
        Date accessTokenExpiryDate = new Date(iat.getTime() + accessTokenExpirationInMs);
        Map<String, Object> headers = new HashMap<>();
        Map<String, Object> claims = new HashMap<>();

        claims.put("type", "access");

        return Jwts.builder()
                .issuer(issuer)
                .issuedAt(iat)
                .audience().add(audience).and()
                .expiration(accessTokenExpiryDate)
                .subject(username)
                .claims(claims)
                .header().add(headers).and()
                .signWith(KeyLoader.getPrivateKey(privateKeySystemPath), SignatureAlgorithm.RS256)
                .compact();

    }

    public String generateRefreshToken(String username) {

        Date iat = new Date();
        long refreshTokenExpirationInMs = Long.valueOf(refreshTokenExpirationInSec) * 1000L;
        Date refreshTokenExpiryDate = new Date(iat.getTime() + refreshTokenExpirationInMs);

        Map<String, Object> headers = new HashMap<>();
        Map<String, Object> claims = new HashMap<>();

        claims.put("type", "refresh");

        return Jwts.builder()
                .issuer(issuer)
                .issuedAt(iat)
                .audience().add(audience).and()
                .expiration(refreshTokenExpiryDate)
                .subject(username)
                .claims(claims)
                .header().add(headers).and()
                .signWith(KeyLoader.getPrivateKey(privateKeySystemPath), SignatureAlgorithm.RS256)
                .compact();

    }

    public Jws<Claims> validateAndParseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(KeyLoader.getPublicKey(publicKeySystemPath))
                    .build()
                    .parseSignedClaims(token);
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
            throw new JwtAuthenticationException("Invalid JWT signature", ex);
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
            throw new JwtAuthenticationException("Invalid JWT token", ex);
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
            throw new TokenExpiredException(
                    "JWT token has expired. Please login again or refresh your token.",
                    token,
                    ex.getClaims().getExpiration()
            );
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
            throw new JwtAuthenticationException("Unsupported JWT token", ex);
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
            throw new JwtAuthenticationException("JWT claims string is empty", ex);
        }
    }


    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(KeyLoader.getPublicKey(publicKeySystemPath))
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.debug("Token validation failed: {} ", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(KeyLoader.getPublicKey(publicKeySystemPath))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration();
        } catch (Exception e) {
            log.error("Failed getting expiry date from token");
            throw new RuntimeException(e);
        }
    }

    public static class KeyLoader {
        public static PublicKey getPublicKey(String keyPath) {
            try {
                String key = Files.readString(Paths.get(keyPath))
                        .replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "")
                        .replaceAll("\\s", "");

                byte[] keyByte = Base64.getDecoder().decode(key);
                X509EncodedKeySpec spec = new X509EncodedKeySpec(keyByte);
                return KeyFactory.getInstance("RSA").generatePublic(spec);
            } catch (Exception e) {
                log.error("Failed creating public key.");
                throw new RuntimeException(e);
            }
        }

        public static PrivateKey getPrivateKey(String keyPath) {
            try {
                String key = Files.readString(Paths.get(keyPath))
                        .replace("-----BEGIN PRIVATE KEY-----", "")
                        .replace("-----END PRIVATE KEY-----", "")
                        .replaceAll("\\s", "");

                byte[] keyByte = Base64.getDecoder().decode(key);
                PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyByte);
                return KeyFactory.getInstance("RSA").generatePrivate(spec);
            } catch (Exception e) {
                log.error("Failed creating private key");
                throw new RuntimeException(e);
            }
        }
    }
}
