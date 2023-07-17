package com.github.yuliyadzemidovich.parceldeliveryapp.filter;

import com.github.yuliyadzemidovich.parceldeliveryapp.repository.UserRepository;
import com.github.yuliyadzemidovich.parceldeliveryapp.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.API_VERSION;
import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.OPENAPI_BASE_URL;
import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.PATH_ACTUATOR_HEALTH;
import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.LOGIN;
import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.PREFIX_BEARER;
import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.REGISTER;
import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.SWAGGER_BASE_URL;
import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.USER;

/**
 * Security Filter for JWT.
 */
@Component
@Slf4j
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final JwtService jwtService;
    private final UserRepository userRepo;
    private final UserDetailsService userDetailsService;

    @Autowired
    public JWTAuthorizationFilter(AuthenticationManager authenticationManager,
                                  JwtService jwtService,
                                  UserRepository userRepo,
                                  UserDetailsService userDetailsService) {
        super(authenticationManager);
        this.jwtService = jwtService;
        this.userRepo = userRepo;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String path = req.getServletPath();
        if (inAllowedList(path)) {
            chain.doFilter(req, res);
            return;
        }
        final String authorizationHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(PREFIX_BEARER)) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String jwt = extractToken(authorizationHeader);
        if (!jwtService.isValidToken(jwt)) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String email = jwtService.extractUserEmail(jwt);
        if (!userRepo.existsByEmail(email)) {
            log.warn("Got JWT with user email {} - user does not exist", email);
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        // init UsernamePasswordAuthenticationToken which sets super.setAuthenticated(true) in the constructor
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        var token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
        // give the authentication token to Spring Security Context
        SecurityContextHolder.getContext().setAuthentication(token);
        chain.doFilter(req, res);
    }

    private String extractToken(String authorizationHeader) {
        return authorizationHeader.split(" ")[1];
    }

    private boolean inAllowedList(String path) {
        return path.startsWith(PATH_ACTUATOR_HEALTH)
                || path.startsWith(OPENAPI_BASE_URL)
                || path.startsWith(SWAGGER_BASE_URL)
                || path.startsWith(API_VERSION + LOGIN)
                || path.startsWith(API_VERSION + USER + REGISTER);
    }
}
