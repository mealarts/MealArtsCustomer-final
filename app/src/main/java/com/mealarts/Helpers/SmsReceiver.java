package com.mealarts.Helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.Objects;


public class SmsReceiver extends BroadcastReceiver
{

    private static SMSListener mListener;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle data  = intent.getExtras();

        Object[] pdus = new Object[0];
        if (data != null) {
            pdus = (Object[]) data.get("pdus"); // the pdus key will contain the newly received SMS
        }

        for (Object o : Objects.requireNonNull(pdus)) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) o);

            String sender = smsMessage.getDisplayOriginatingAddress();
            //You must check here if the sender is your provider and not another one with same text.
            Log.e("otp_sender", sender);

            String messageBody = smsMessage.getMessageBody();

            //Pass on the text to our listener.
            try {
                mListener.messageReceived(messageBody);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void bindListener(SMSListener listener)
	{
        mListener = listener;
    }
}