
package com.carit.imhere.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMS_Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.e("SMS_Receiver","收到短信");
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            // 不知道为什么明明只有一条消息，传过来的却是数组，也许是为了处理同时同分同秒同毫秒收到多条短信
            // 但这个概率有点小
            SmsMessage[] message = new SmsMessage[pdus.length];
            StringBuilder sb = new StringBuilder();
            Log.e("SMS_Receiver","pdus长度" + pdus.length);
            for (int i = 0; i < pdus.length; i++) {
                // 虽然是循环，其实pdus长度一般都是1
                message[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                sb.append("接收到短信来自:\n");
                sb.append(message[i].getDisplayOriginatingAddress() + "\n");
                sb.append("内容:" + message[i].getDisplayMessageBody());
                String body =  message[i].getDisplayMessageBody();
                //if("#navi#");
            }
            Log.e("SMS_Receiver",sb.toString());
        }
    }
}
