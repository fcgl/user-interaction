package com.fcgl.madrid.user.service;

import com.fcgl.madrid.user.dataModel.AuthProvider;
import com.fcgl.madrid.user.dataModel.User;
import com.fcgl.madrid.user.exception.BadRequestException;
import com.fcgl.madrid.user.payload.InternalStatus;
import com.fcgl.madrid.user.payload.StatusCode;
import com.fcgl.madrid.user.payload.response.ApiResponse;
import com.fcgl.madrid.user.payload.response.AuthResponse;
import com.fcgl.madrid.user.payload.request.LoginRequest;
import com.fcgl.madrid.user.payload.request.SignUpRequest;
import com.fcgl.madrid.user.payload.response.LoginResponse;
import com.fcgl.madrid.user.repository.UserRepository;
import com.fcgl.madrid.user.security.TokenProvider;
import com.fcgl.madrid.user.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.net.URI;
import org.springframework.security.authentication.BadCredentialsException;


@Service
public class AuthService {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private TokenProvider tokenProvider;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository,
                       PasswordEncoder passwordEncoder, TokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    /**
     * TODO: Add Resilience4j, There could be SQL errors that we need to catch
     * Authenticates the user
     * @param loginRequest request params
     * @return ResponseEntity
     */
    public ResponseEntity<LoginResponse> authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
        } catch(BadCredentialsException e) {
            InternalStatus internalStatus = new InternalStatus(StatusCode.AUTH_ERROR, HttpStatus.UNAUTHORIZED, e.getMessage());
            return new ResponseEntity<LoginResponse>(new LoginResponse(internalStatus, null), HttpStatus.UNAUTHORIZED);
        }


        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String userId = Long.toString(userPrincipal.getId());
        AuthResponse authResponse = new AuthResponse(token, userId);
        LoginResponse response = new LoginResponse(InternalStatus.OK, authResponse);
        return new ResponseEntity<LoginResponse>(response ,HttpStatus.OK);
    }

    public ResponseEntity<?> registerUser(SignUpRequest signUpRequest) {
        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email address already in use.");
        }

        // Creating user's account
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(signUpRequest.getPassword());
        user.setProvider(AuthProvider.local);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "User registered successfully@"));
    }
}
