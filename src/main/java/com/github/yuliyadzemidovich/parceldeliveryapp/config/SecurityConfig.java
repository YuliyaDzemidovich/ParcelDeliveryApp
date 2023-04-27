package com.github.yuliyadzemidovich.parceldeliveryapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Configure HTTP Security
     * @param http HttpSecurity object
     * @return SecurityFilterChain
     * @throws Exception security configuration exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // disable CSRF as we do not serve browser clients
            .csrf().disable()
            // allow access restriction using request matcher
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            // make sure we use stateless session, session will not be used to store user's state
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }

    /**
     * Defines Password Encoder Bean - the way how user password should be encoded/hashed when it is stored in the database.
     * @return a specific type of Password Encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * Global AuthenticationManager, that is exposed globally, so it is available anywhere in the application.
     *
     * @param authenticationConfiguration Exports the authentication Configuration
     * @return AuthenticationManager bean
     * @throws Exception exception during bean initialization
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
