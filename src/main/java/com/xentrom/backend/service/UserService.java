package com.xentrom.backend.service;

import com.xentrom.backend.dto.UserDto;
import com.xentrom.backend.dto.input.AuthInput;
import com.xentrom.backend.dto.input.RegisterInput;
import com.xentrom.backend.model.User;
import com.xentrom.backend.model.mapper.UserMapper;
import com.xentrom.backend.repository.UserRepository;
import com.xentrom.backend.security.JwtUtil;
import com.xentrom.backend.dto.output.*;
import com.xentrom.backend.utils.Commons;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Optional;


@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtTokenUtil;
    private final DBUserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserMapper userMapper;

    private final static String USERNAME_EXISTS = "Username already exists!";
    private final static String USERNAME_NOT_FOUND = "Username not found!";
    private final static String REGISTERING_ERROR = "An error occurred while registering the user!";
    private final static String INCORRECT_CREDS = "Incorrect username or password!";
    private final static String INSUFFICIENT_PERMISSIONS = "You don't have sufficient permissions!";
    private final static String ERROR = "Error!";

    public RegisterOutput registerUser(RegisterInput registerInput) {
        RegisterOutput result = new RegisterOutput();

        Optional<User> optionalUser = userRepository.findByUsername(registerInput.getUsername());
        if (optionalUser.isPresent()) {
            result.setStatusEnum(Output.StatusEnum.ERROR);
            result.addMessage(USERNAME_EXISTS);
            return result;
        }

        User newUser = new User(
                0,
                registerInput.getUsername(),
                registerInput.getEmail(),
                bCryptPasswordEncoder.encode(registerInput.getPassword()),
                User.RoleEnum.USER);

        newUser = userRepository.save(newUser);
        if (newUser.getId() == 0) {
            result.setStatusEnum(Output.StatusEnum.ERROR);
            result.addMessage(REGISTERING_ERROR);
        } else {
            UserDto userDto = userMapper.userToUserDto(newUser);
            userDto.setPassword(null);

            result.setUser(userDto);
        }

        return result;
    }

    public AuthOutput authenticateUser(AuthInput authInput) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authInput.getUsername(), authInput.getPassword())
            );
        }
        catch (BadCredentialsException e) {
            AuthOutput result = new AuthOutput();
            result.setStatusEnum(Output.StatusEnum.ERROR);
            result.addMessage(INCORRECT_CREDS);

            return result;
        }


        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authInput.getUsername());

        final String jwt = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername(authInput.getUsername()).orElseThrow();
        UserDto userDto = userMapper.userToUserDto(user);
        userDto.setPassword(null);

        return new AuthOutput(jwt, userDto);
    }

    public Output createOrUpdateUser(UserDto userDto) {
        Output output = new Output();
        User user;
        String formerPassword = null;

        if (userDto.getId() != null && userDto.getId() != 0) {
            user = userRepository.findById(userDto.getId())
                    .orElseThrow();

            formerPassword = user.getPassword();
        }

        user = mapUserAndComputePassword(userDto, formerPassword);
        userRepository.saveAndFlush(user);

        return output;
    }

    public Output selfUpdate(UserDto userDto) {
        Output output = new Output();

        UserDetails userDetails = Commons.getCurUserDetails();
        if (!userDetails.getUsername().equals(userDto.getUsername())) {
            output.setStatusEnum(Output.StatusEnum.ERROR);
            output.addMessage(ERROR);
            return output;
        }

        User.RoleEnum curUserRole = Commons.getCurUserRole();
        if (firstRoleIsLowerThanSecond(curUserRole, userDto.getRole())) {
            output.setStatusEnum(Output.StatusEnum.ERROR);
            output.addMessage(INSUFFICIENT_PERMISSIONS);
            return output;
        }

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        String formerPassword = user.getPassword();

        user = mapUserAndComputePassword(userDto, formerPassword);
        userRepository.saveAndFlush(user);

        return output;
    }

    public Output deleteUser(Integer userId) {
        userRepository.deleteById(userId);
        return new Output();
    }

    public GetUserOutput getUser(Integer userId) {
        GetUserOutput output = new GetUserOutput();

        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            output.setStatusEnum(Output.StatusEnum.ERROR);
            output.addMessage(USERNAME_NOT_FOUND);
            return output;
        }

        UserDto userDto = userMapper.userToUserDto(userOpt.get());
        userDto.setPassword(null);
        output.setUser(userDto);

        return output;
    }

    public GetUsersOutput getUsers() {
        GetUsersOutput output = new GetUsersOutput();
        UserDto[] users = userRepository
                .findAll()
                .stream()
                .map(userMapper::userToUserDto)
                .peek(userDto -> userDto.setPassword(null))
                .toArray(UserDto[]::new);

        output.setUsers(users);
        return output;
    }

    private User mapUserAndComputePassword(UserDto userDto, String formerPassword) {
        User user = userMapper.userDtoToUser(userDto);
        if (ObjectUtils.isEmpty(user.getPassword())) {
            user.setPassword(formerPassword);
        } else {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }

        return user;
    }

    private boolean firstRoleIsLowerThanSecond(User.RoleEnum first, User.RoleEnum second) {
        if (first.equals(User.RoleEnum.USER) && second.equals(User.RoleEnum.ADMIN)) {
            return true;
        }

        return false;
    }
}
