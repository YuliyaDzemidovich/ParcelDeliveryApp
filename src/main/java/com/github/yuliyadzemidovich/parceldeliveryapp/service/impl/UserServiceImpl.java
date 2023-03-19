package com.github.yuliyadzemidovich.parceldeliveryapp.service.impl;

import com.github.yuliyadzemidovich.parceldeliveryapp.dto.UserDto;
import com.github.yuliyadzemidovich.parceldeliveryapp.entity.Role;
import com.github.yuliyadzemidovich.parceldeliveryapp.entity.User;
import com.github.yuliyadzemidovich.parceldeliveryapp.exception.ValidationException;
import com.github.yuliyadzemidovich.parceldeliveryapp.repository.UserRepository;
import com.github.yuliyadzemidovich.parceldeliveryapp.service.DtoMapper;
import com.github.yuliyadzemidovich.parceldeliveryapp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String MSG_ROLE_NOT_FOUND = "User role not recognized";

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getId() != null) {
            throw new ValidationException("Cannot create user with specific ID - please remove ID from the body", HttpStatus.BAD_REQUEST);
        }
        User user = DtoMapper.mapToUser(userDto);
        verifyAndMapRole(userDto, user);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepo.save(user);
        log.info("Created user with id={} and role={}", user.getId(), user.getRole().getSecurityValue());
        userDto = DtoMapper.mapToUserDto(user);
        return userDto;
    }

    private void verifyAndMapRole(UserDto userDto, User user) {
        String roleReq = userDto.getRole();
        Role role;
        if (roleReq == null) {
            role = Role.ROLE_USER;
        } else {
            Optional<Role> roleMatched = Role.findRoleByShortValue(roleReq);
            if (roleMatched.isEmpty()) {
                throw new ValidationException(MSG_ROLE_NOT_FOUND, HttpStatus.BAD_REQUEST);
            }
            role = roleMatched.get();
        }

        // creating super admin not permitted
        if (role == Role.ROLE_SUPER_ADMIN) {
            // msg is the same as role not found - so that super admin role existence will not be exposed
            throw new ValidationException(MSG_ROLE_NOT_FOUND, HttpStatus.BAD_REQUEST);
        } else {
            user.setRole(role);
        }
    }
}
