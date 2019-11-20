package com.fcgl.madrid.user.controller;

import com.fcgl.madrid.user.payload.request.LoginRequest;
import com.fcgl.madrid.user.payload.request.SignUpRequest;
import com.fcgl.madrid.user.payload.response.LoginResponse;
import com.fcgl.madrid.user.service.AuthService;
import com.fcgl.madrid.user.service.twilio.TwilioSms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;
    private TwilioSms twilioSms;

    @Autowired
    public void setAuthService(AuthService authService, TwilioSms twilioSms) {
        this.authService = authService;
        this.twilioSms = twilioSms;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        return authService.registerUser(signUpRequest);
    }

    @PostMapping("/sendSms")
    public ResponseEntity<?> sendSms() {
        return new ResponseEntity<String>(twilioSms.sendSms("Testing SMS", "+18574078438"), HttpStatus.OK);
    }
}
