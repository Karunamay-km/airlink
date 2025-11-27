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

        log.info("🔍 Starting JwtAuthenticationFilter for request: {}", request.getRequestURI());

        try {
            String token = null;
            String header = request.getHeader("Authorization");

            log.info("Authorization Header: {}", header);

            if (header != null && header.startsWith("Bearer ")) {
                token = header.substring(7);
                log.info("Extracted Token from Authorization Header");
            }

            if (token == null) {
                log.info("Token not found in header. Checking cookies...");
                Cookie[] cookies = request.getCookies();

                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        log.info("Cookie found: {}", cookie.getName());
                        if ("accessToken".equals(cookie.getName())) {
                            token = cookie.getValue();
                            log.info("Extracted Token from Cookies");
                            break;
                        }
                    }
                } else {
                    log.info("No cookies found.");
                }
            }

            log.info("Token to validate: {}", token);

            Optional<BlackListToken> isBlackListedToken = blackListTokenRepository.findByTokenId(token);
            log.info("Blacklist check result: {}", isBlackListedToken.isPresent() ? "BLACKLISTED" : "Not blacklisted");

            if (token != null && isBlackListedToken.isEmpty()) {

                log.info("Validating and parsing token");
                Claims claims = jwtTokenProvider.validateAndParseClaims(token).getPayload();

                String username = claims.getSubject();
                log.info("Extracted Username from Token: {}", username);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                log.info("Loaded UserDetails for: {}", username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("Authentication set successfully for user: {}", username);
            } else {
                log.info("Token is either null or blacklisted. Skipping authentication setup.");
            }

            filterChain.doFilter(request, response);
            log.info("Filter chain continued successfully.");

        } catch (TokenExpiredException e) {
            log.error("❌ TokenExpiredException: Token expired", e);
            handlerExceptionResolver.resolveException(request, response, null, e);
        } catch (Exception e) {
            log.error("❌ Unexpected authentication error", e);
            handlerExceptionResolver.resolveException(request, response, null, e);
        }

        log.info("JwtAuthenticationFilter execution completed for request: {}", request.getRequestURI());
    }
}

