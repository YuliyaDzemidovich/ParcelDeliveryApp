package com.github.yuliyadzemidovich.parceldeliveryapp.config;

import com.github.yuliyadzemidovich.parceldeliveryapp.entity.User;
import com.github.yuliyadzemidovich.parceldeliveryapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    public static final String USER_NOT_FOUND = "User not found";

    private final UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (!StringUtils.hasText(email)) {
            throw new UsernameNotFoundException(USER_NOT_FOUND);
        }
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(USER_NOT_FOUND);
        }
        AppUserDetails userDetails = new AppUserDetails();
        userDetails.setUsername(user.getEmail());
        userDetails.setPassword(user.getPassword());
        userDetails.setRole(user.getRole().getSecurityValue());
        return userDetails;
    }
}
