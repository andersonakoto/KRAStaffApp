package com.example.krastaffapp.interfaces;

public interface OTPInterface {
        void onOtpReceived(String otp);
        void onOtpTimeout();
}
