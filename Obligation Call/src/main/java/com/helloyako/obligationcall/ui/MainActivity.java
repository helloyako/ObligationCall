package com.helloyako.obligationcall.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.helloyako.obligationcall.ObligationCallCommon;
import com.helloyako.obligationcall.R;
import com.helloyako.obligationcall.data.ObligationDatasource;
import com.helloyako.obligationcall.model.CallInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {

    private SharedPreferences prefs;
    private ObligationDatasource dataSource;

    private String phoneNumberKey;
    private String isSettingKey;
    private ProgressDialog progressDialog;

    private List<CallInfo> callInfoList;

    private Handler resetHandler = new Handler(){
        public void handleMessage(Message msg){
            progressDialog.dismiss();
        }
    };

    private Handler drawUiHandler = new Handler(){
        public void handleMessage(Message msg){

            LinearLayout mainLinearLayout = (LinearLayout) findViewById(R.id.MainLinearLayout);

            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            for (int i = 0; i < callInfoList.size(); i++){
                @SuppressLint("InflateParams") LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.call_info_layout,null);
                CallInfo callInfo = callInfoList.get(i);
                LinearLayout linearLayout = (LinearLayout) ll.getChildAt(0);
                TextView tv = (TextView) linearLayout.getChildAt(0);
                Date date = new Date(callInfo.getCallTime());
                DateFormat df = new SimpleDateFormat(getApplicationContext().getString(R.string.date_format));
                tv.setText(df.format(date));
                ImageView iv = (ImageView) ll.getChildAt(1);
                if(callInfo.isDone()) {
                    iv.setVisibility(View.VISIBLE);
                }
                mainLinearLayout.addView(ll, i);
            }
            progressDialog.dismiss();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataSource = new ObligationDatasource(this);
        dataSource.open();

        final TextView signUpText = (TextView) findViewById(R.id.main_digi_text);
        signUpText.setMovementMethod(LinkMovementMethod.getInstance());
        signUpText.setText(Html.fromHtml(getString(R.string.digi_link)));

        final TextView giveText = (TextView) findViewById(R.id.main_give_text);
        giveText.setMovementMethod(LinkMovementMethod.getInstance());
        giveText.setText(Html.fromHtml(getString(R.string.give_text)));

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        phoneNumberKey = getString(R.string.preferences_phone_number_key);
        isSettingKey = getString(R.string.preferences_is_setting_key);

        if(!prefs.getBoolean(isSettingKey,false)){
            startSettingActiviry();
        } else {
            drawCallInfoList();
        }

    }

    public void onClickResetSettingButton(final View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String alertMessage = getString(R.string.alert_message);
        builder.setMessage(alertMessage).setCancelable(false).setPositiveButton("YES",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                resetSetting();
            }
        }).setNegativeButton("NO",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void startSettingActiviry(){
        Intent i = new Intent(this,SettingActivity.class);
        startActivity(i);
        finish();
    }

    private void resetSetting() {
        progressDialog = ProgressDialog.show(this,getString(R.string.app_name),getString(R.string.progress_dialog_reset_message),true);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences.Editor prefsEditor = prefs.edit();
                prefsEditor.putString(phoneNumberKey, "");
                prefsEditor.putBoolean(isSettingKey, false);
                prefsEditor.commit();

                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                String phoneNumber = prefs.getString(phoneNumberKey,"");

                List<CallInfo> callInfoList = dataSource.getAllCallInfo();

                for(CallInfo callInfo : callInfoList){
                    if(!callInfo.isDone()) {
                        PendingIntent pendingIntent = ObligationCallCommon.getPendingIntentForAlarmReceiver(getApplicationContext(), phoneNumber, callInfo.getIndex());
                        am.cancel(pendingIntent);
                    }
                }

                dataSource.deleteAll();

                startSettingActiviry();
                resetHandler.sendEmptyMessage(0);
            }
        });

        thread.start();
    }

    private void drawCallInfoList(){
        progressDialog = ProgressDialog.show(MainActivity.this,getString(R.string.app_name),getString(R.string.progress_dialog_loading_message),true);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                callInfoList = dataSource.getAllCallInfo();
                drawUiHandler.sendEmptyMessage(0);
            }
        });

        thread.start();
    }

    @Override
    protected void onDestroy() {
        dataSource.close();
        super.onDestroy();
    }
}
