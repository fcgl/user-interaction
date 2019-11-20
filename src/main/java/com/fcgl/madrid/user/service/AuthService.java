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
import com.fcgl.madrid.user.service.twilio.TwilioSms;
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
    private OtpService otpService;
    private TwilioSms twilioSms;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository,
                       PasswordEncoder passwordEncoder, TokenProvider tokenProvider, OtpService otpService,
                       TwilioSms twilioSms) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.otpService = otpService;
        this.twilioSms = twilioSms;
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
                            loginRequest.getPhoneNumber(),
                            loginRequest.getPassword()
                    )
            );
        } catch(BadCredentialsException e) {
            InternalStatus internalStatus = new InternalStatus(StatusCode.AUTH_ERROR, HttpStatus.UNAUTHORIZED, e.getMessage());
            return new ResponseEntity<LoginResponse>(new LoginResponse(internalStatus, null), HttpStatus.UNAUTHORIZED);
        }


        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(authentication);
        AuthResponse authResponse = new AuthResponse(token);
        LoginResponse response = new LoginResponse(InternalStatus.OK, authResponse);
        return new ResponseEntity<LoginResponse>(response ,HttpStatus.OK);
    }

    public ResponseEntity<?> registerUser(SignUpRequest signUpRequest) {
//        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
//            throw new BadRequestException("Email address already in use.");
//        }

        if (userRepository.existsByPhoneNumber(signUpRequest.getPhoneNumber())) {
            throw new BadRequestException("Phone Number already in use.");
        }
        String otpValue = otpService.generateOtp();
        // Creating user's account
        User user = new User();
        user.setName(signUpRequest.getName());
//        user.setEmail(signUpRequest.getEmail());
        user.setPhoneNumber(signUpRequest.getPhoneNumber());
        user.setPassword(otpValue);
        user.setProvider(AuthProvider.local);

        user.setPassword(passwordEncoder.encode(otpValue));

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.getId()).toUri();
        //TODO: Phone number error handling
        System.out.println("Before twilio");
        twilioSms.sendSms("Cowboy OTP: " + otpValue, signUpRequest.getPhoneNumber());
        System.out.println("After twilio");
        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "User registered successfully, Verify OTP to complete registration"));
    }
}
