package com.github.yuliyadzemidovich.parceldeliveryapp.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;

import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.API_VERSION;
import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.USER;

/**
 * Security Filter for JWT.
 */
@Component
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String path = req.getServletPath();
        if (inAllowedList(path)) {
            chain.doFilter(req, res);
            return;
        }
        final String authorizationHeader = req.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(req, res);
            return;
        }
        // todo decode jwt from authorization header
        // todo add if jwt valid/invalid
        String username = "user1"; // todo unmock
        // init UsernamePasswordAuthenticationToken which sets super.setAuthenticated(true) in the constructor
        var token = new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
        // give the authentication token to Spring Security Context
        SecurityContextHolder.getContext().setAuthentication(token);
        chain.doFilter(req, res);
    }

    private boolean inAllowedList(String path) {
        return path.startsWith("/actuator/health")
                || path.startsWith(API_VERSION + "/login")
                || path.startsWith(API_VERSION + USER + "/register");
    }
}
