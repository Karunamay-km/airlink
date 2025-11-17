package com.karunamay.airlink.service.security;

import com.karunamay.airlink.exceptions.TokenExpiredException;
import com.karunamay.airlink.model.token.BlackListToken;
import com.karunamay.airlink.repository.token.BlackListTokenRepository;
import com.karunamay.airlink.service.user.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final BlackListTokenRepository blackListTokenRepository;

    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = null;
            String header = request.getHeader("Authorization");

            if (header != null && header.startsWith("Bearer ")) {
                token = header.substring(7);
            }

            if (token == null) {
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {


                    for (Cookie cookie : cookies) {
                        if ("accessToken".equals(cookie.getName())) {
                            token = cookie.getValue();
                            break;
                        }
                    }
                }
            }

            Optional<BlackListToken> isBlackListedToken = blackListTokenRepository.findByTokenId(token);

            if (token != null && isBlackListedToken.isEmpty()) {

                Claims claims = jwtTokenProvider.validateAndParseClaims(token).getPayload();
                String username = claims.getSubject();

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Set authentication for user: {}", username);

            }
            filterChain.doFilter(request, response);
        } catch (TokenExpiredException e) {
            log.error("Could not set user authentication in security context", e);
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}
