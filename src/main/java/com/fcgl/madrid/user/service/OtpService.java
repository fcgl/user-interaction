package com.fcgl.madrid.user.service;

import org.springframework.stereotype.Service;

@Service
public class OtpService {

    public OtpService() {

    }

    /**
     * Generates an OTP value, sets OTP value for user
     * TODO: Create a more secure OTP workflowg
     * @return String: OTP value
     */
    public String generateOtp() {
        int randomPin   =(int) (Math.random()*9000)+1000;
        String otp  = String.valueOf(randomPin);
        return otp; //returning value of otp
    }

    public String validateOtp() {
        return "";
    }


}
