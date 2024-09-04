package com.my.afarycode.OnlineShopping.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySMSBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Broadcast receiver", "onReceive: ");

/*
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            switch(status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // (Optional) Get SMS Sender address - only available in
                    // GMS version 24.20 onwards, else it will return null
                   // String senderAddress = extras.getString(SmsRetriever.EXTRA_SMS_ORIGINATING_ADDRESS);
                    // Get SMS message contents
                    String message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE);
                    // Extract one-time code from the message and complete verification
                    // by sending the code back to your server.
                    Log.e("check otp msg====",message);
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
                    break;
            }
        }
*/





        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            // Retrieve SMS message
            Status status = (Status) intent.getParcelableExtra(SmsRetriever.EXTRA_STATUS);
            if (status.getStatusCode() == CommonStatusCodes.SUCCESS) {
                // Get SMS message
                Intent messageIntent = intent.getParcelableExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                if (messageIntent != null) {
                    String message = messageIntent.getStringExtra("message");
                    if (message != null) {
                        Log.d("SmsReceiver", "Received SMS message: " + message);
                        // Extract OTP from the message
                        String otp = extractOtpFromMessage(message);
                        Log.d("SmsReceiver", "Extracted OTP: " + otp);
                    }
                }
            } else {
                Log.d("SmsReceiver", "Failed to retrieve SMS: " + status.getStatusCode());
            }
        }



    }


    private String extractOtpFromMessage(String message) {
        // Implement logic to extract OTP from the message
        // For example, if OTP is a 6-digit number in the message
        String otp = null;
        // Simple example to extract a 6-digit OTP
        Pattern pattern = Pattern.compile("\\b\\d{6}\\b");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            otp = matcher.group();
        }
        return otp;
    }
}