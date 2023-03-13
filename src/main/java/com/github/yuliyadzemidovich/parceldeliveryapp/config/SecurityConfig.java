package com.github.yuliyadzemidovich.parceldeliveryapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configure HTTP Security
     * @param http HttpSecurity object
     * @return SecurityFilterChain
     * @throws Exception security configuration exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin()
                .and()
                .logout()
                .deleteCookies("JSESSIONID");
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
}
