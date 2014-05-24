package com.helloyako.obligationcall.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.helloyako.obligationcall.free.R;
import com.helloyako.obligationcall.data.ObligationDatasource;

public class AlarmCallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ObligationDatasource dataSource = new ObligationDatasource(context);
        dataSource.open();

        Bundle bundle = intent.getExtras();
        String indexKey = context.getString(R.string.bundle_index_key);
        int index = bundle.getInt(indexKey);
        String phoneNumberKey = context.getString(R.string.bundle_phone_number_key);
        String phoneNumber = bundle.getString(phoneNumberKey);

        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Log.d("AlarmRecevier","index : " + String.valueOf(index));
        dataSource.updateCall(index, true);

        context.startActivity(callIntent);



        dataSource.close();
    }
}
