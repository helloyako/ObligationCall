package com.helloyako.obligationcall;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.helloyako.obligationcall.model.CallInfo;
import com.helloyako.obligationcall.receiver.AlarmCallReceiver;

import com.helloyako.obligationcall.R;

import java.util.List;

/**
 * Created by helloyako on 2014. 5. 21..
 *
 */
public class ObligationCallCommon {

    public static PendingIntent getPendingIntentForAlarmReceiver(Context context,String phoneNumber, int index){
        Intent intent = new Intent(context, AlarmCallReceiver.class);

        Bundle bundle = new Bundle();
        String phoneNumberKey = context.getString(R.string.bundle_phone_number_key);
        String indexKey = context.getString(R.string.bundle_index_key);
        bundle.putString(phoneNumberKey, phoneNumber);
        bundle.putInt(indexKey, index);
        intent.putExtras(bundle);

        return PendingIntent.getBroadcast(context, index , intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public static void registerCallInfo(Context context, List<CallInfo> callInfoList, String phoneNumber){

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for(CallInfo callInfo : callInfoList){
            if(!callInfo.isDone()){
                PendingIntent pendingIntent = getPendingIntentForAlarmReceiver(context, phoneNumber, callInfo.getIndex());
                am.set(AlarmManager.RTC_WAKEUP, callInfo.getCallTime(), pendingIntent);
            }
        }

    }

}
