package com.helloyako.obligationcall.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.helloyako.obligationcall.ObligationCallCommon;
import com.helloyako.obligationcall.R;
import com.helloyako.obligationcall.data.ObligationDatasource;
import com.helloyako.obligationcall.model.CallInfo;

import java.util.List;
import java.util.TimeZone;


public class SettingActivity extends Activity {

    private EditText phoneNumberEditText;
    private EditText monthsEditText;
    private EditText callsEditText;
    private EditText timesEditText;

    private Button submitButton;

    private SharedPreferences prefs;

    private ObligationDatasource dataSource;

    private ProgressDialog progressDialog;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            Toast.makeText(getApplicationContext(), getString(R.string.toast_setting_done), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        dataSource = new ObligationDatasource(this);
        dataSource.open();

        phoneNumberEditText = (EditText) findViewById(R.id.PhoneNumberEditText);
        monthsEditText = (EditText) findViewById(R.id.MonthsEditText);
        callsEditText = (EditText) findViewById(R.id.CallsEditText);
        timesEditText = (EditText) findViewById(R.id.TimesEditText);

        submitButton = (Button) findViewById(R.id.SubmitButton);

        TextWatcher watcher = validationTextWatcher();

        phoneNumberEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        phoneNumberEditText.addTextChangedListener(watcher);
        monthsEditText.addTextChangedListener(watcher);
        callsEditText.addTextChangedListener(watcher);
        timesEditText.addTextChangedListener(watcher);

        monthsEditText.addTextChangedListener(monthsTextWatcher());
        callsEditText.addTextChangedListener(callsTextWatcher());
        timesEditText.addTextChangedListener(timesTextWatcher());

        final TextView signUpText = (TextView) findViewById(R.id.setting_digi_text);
        signUpText.setMovementMethod(LinkMovementMethod.getInstance());
        signUpText.setText(Html.fromHtml(getString(R.string.digi_link)));

        final TextView giveText = (TextView) findViewById(R.id.setting_give_text);
        giveText.setMovementMethod(LinkMovementMethod.getInstance());
        giveText.setText(Html.fromHtml(getString(R.string.give_text)));

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public void onClickSubmitButton(final View view){
        progressDialog = ProgressDialog.show(this,getString(R.string.app_name),getString(R.string.progress_dialog_setting_message),true);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences.Editor prefsEditor = prefs.edit();
                String phoneNumberKey = getString(R.string.preferences_phone_number_key);
                String phoneNumber = phoneNumberEditText.getText().toString();
                prefsEditor.putString(phoneNumberKey, phoneNumber);
                String isSettingKey = getString(R.string.preferences_is_setting_key);
                prefsEditor.putBoolean(isSettingKey, true);
                prefsEditor.commit();

                setAlarm(phoneNumber);
                startMainActiviry();
                handler.sendEmptyMessage(0);
            }
        });

        thread.start();
    }

    private void setAlarm(String phoneNumber) {
        int months = Integer.parseInt(monthsEditText.getText().toString());
        int calls = Integer.parseInt(callsEditText.getText().toString());
        int times = Integer.parseInt(timesEditText.getText().toString());

        long oneHourToMillis = (long)60 * 60 * 1000;
        long oneDayToMillis = (long)24 * oneHourToMillis;
        long oneMonthsToMillis = (long)30 * oneDayToMillis;
        long rawOffset = (long)TimeZone.getDefault().getRawOffset();

        long currentMillis = System.currentTimeMillis();

        for(int i = 0; i < months ; i ++){
            long setTimeMillis = currentMillis + (i * oneMonthsToMillis);
            setTimeMillis = setTimeMillis / oneDayToMillis;
            setTimeMillis = setTimeMillis * oneDayToMillis;
            setTimeMillis = setTimeMillis - rawOffset;
            setTimeMillis = setTimeMillis + (oneHourToMillis * times);
            for(int j = 0; j < calls; j++){
                setTimeMillis = setTimeMillis + oneDayToMillis;
                dataSource.createCall(setTimeMillis);
            }
        }

        List<CallInfo> callInfoList = dataSource.getAllCallInfo();

        ObligationCallCommon.registerCallInfo(this, callInfoList, phoneNumber);
    }

    private TextWatcher validationTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(final Editable gitDirEditText) {
                updateUIWithValidation();
            }

        };
    }

    private TextWatcher monthsTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(final Editable gitDirEditText) {
                if(gitDirEditText.length() > 0) {
                    int months = Integer.parseInt(gitDirEditText.toString());
                    if (months == 0 || months > 12) {
                        String toastMessage = getApplicationContext().getString(R.string.toast_months_watcher);
                        Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
                        gitDirEditText.clear();
                    }
                }
            }
        };
    }

    private TextWatcher callsTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(final Editable gitDirEditText) {
                if(gitDirEditText.length() > 0) {
                    int calls = Integer.parseInt(gitDirEditText.toString());
                    if (calls == 0 || calls > 30) {
                        String toastMessage = getApplicationContext().getString(R.string.toast_calls_watcher);
                        Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
                        gitDirEditText.clear();
                    }
                }
            }
        };
    }

    private TextWatcher timesTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(final Editable gitDirEditText) {
                if(gitDirEditText.length() > 0) {
                    int times = Integer.parseInt(gitDirEditText.toString());
                    if (times > 23) {
                        String toastMessage = getApplicationContext().getString(R.string.toast_times_watcher);
                        Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
                        gitDirEditText.clear();
                    }
                }
            }

        };
    }

    private void updateUIWithValidation() {
        final boolean populated = populated(phoneNumberEditText) && populated(monthsEditText) && populated(callsEditText) && populated(timesEditText);
        submitButton.setEnabled(populated);
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    private void startMainActiviry(){
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {
        dataSource.close();
        super.onDestroy();
    }
}
