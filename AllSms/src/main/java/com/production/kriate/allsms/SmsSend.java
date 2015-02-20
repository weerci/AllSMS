package com.production.kriate.allsms;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.production.kriate.allsms.db.DbSms;

/*Класс инкапсулирует работу по отправке SMS*/
public class SmsSend {
    public static void Send(Context context, DbSms ds){
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(ds.getPhoneNumber(), null, ds.getTextSms(), null, null);
            Toast.makeText(context.getApplicationContext(), R.string.sms_send_success, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context.getApplicationContext(),R.string.sms_send_failed, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
