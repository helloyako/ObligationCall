package com.helloyako.obligationcall.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.helloyako.obligationcall.ObligationCallCommon;
import com.helloyako.obligationcall.free.R;
import com.helloyako.obligationcall.data.ObligationDatasource;
import com.helloyako.obligationcall.model.CallInfo;

import java.util.List;

public class AlarmBootReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            registerAlarms(context);
        }
    }

    private void registerAlarms(Context context) {
        ObligationDatasource dataSource = new ObligationDatasource(context);
        dataSource.open();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String phoneNumberKey = context.getString(R.string.preferences_phone_number_key);
        String phoneNumber = prefs.getString(phoneNumberKey,"");

        List<CallInfo> callInfoList = dataSource.getAllCallInfo();
        Log.d("AlarmBootReciever","phoneNumber : " + phoneNumber);
        Log.d("AlarmBootReciever","callInfoList size : " + String.valueOf(callInfoList.size()));

        ObligationCallCommon.registerCallInfo(context, callInfoList, phoneNumber);

        dataSource.close();
    }
}
