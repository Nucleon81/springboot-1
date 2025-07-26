package com.practice.project_1.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Random;

@Service
public class OtpService {
    private final HashMap<String, String> otpStore = new HashMap<>();
    private final int OTP_LENGTH = 6;
    private final int OTP_VALIDITY_MINUTES = 5;

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    public String generateOtp(String mobileNumber) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStore.put(mobileNumber, otp);

        // Send OTP via Twilio
        Message.creator(
                new PhoneNumber(mobileNumber),
                new PhoneNumber(twilioPhoneNumber),
                "Welcome to Caroo App, Your OTP is " + otp + ". Valid for " + OTP_VALIDITY_MINUTES + " minutes."
        ).create();

        return otp;
    }

    public boolean validateOtp(String mobileNumber, String otp) {
        String storedOtp = otpStore.get(mobileNumber);
        return storedOtp != null && storedOtp.equals(otp);
    }

    public void clearOtp(String mobileNumber) {
        otpStore.remove(mobileNumber);
    }
}