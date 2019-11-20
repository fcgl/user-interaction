package com.fcgl.madrid.user.service.twilio;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

@Service
public class TwilioSms {
    // Find your Account Sid and Token at twilio.com/console
    public static final String ACCOUNT_SID = "";
    public static final String AUTH_TOKEN  = "";

    // Create a phone number in the Twilio console
    public static final String TWILIO_NUMBER = "+12512559842";

    public String sendSms(String msg, String phoneNumber) {
        //TODO: What type of errors can Twilio and Message packages throw
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(TWILIO_NUMBER),
                msg)
                .create();
        return message.getSid();
    }




//    public static void main(String[] args) {
//        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//        Message message = Message.creator(
//                new PhoneNumber("+18574078438"),
//                new PhoneNumber(TWILIO_NUMBER),
//                "Sample Twilio SMS using Java")
//                .create();
//        System.out.println(message.getSid());
//    }
}
