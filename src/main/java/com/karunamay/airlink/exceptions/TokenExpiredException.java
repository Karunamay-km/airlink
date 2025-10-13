package com.karunamay.airlink.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;

import java.util.Date;

@Getter
//@RequiredArgsConstructor
public class TokenExpiredException extends InsufficientAuthenticationException {

    private final String token;
    private final Date expiredAt;

    public TokenExpiredException(String message, String token, Date expiredAt) {
        super(message);
        this.token = token;
        this.expiredAt = expiredAt;
    }

//    public TokenExpiredException(String message) {
//        super(message);
//    }
//
//    public TokenExpiredException(String message, Throwable cause) {
//        super(message, cause);
//    }
}
