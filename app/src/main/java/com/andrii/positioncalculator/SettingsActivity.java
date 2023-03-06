package com.andrii.positioncalculator;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.andrii.positioncalculator.Helpers.StorageManager;
import com.andrii.positioncalculator.Services.APIFetchService;
import com.example.positioncalculator.R;


public class SettingsActivity extends AppCompatActivity {

    public Button return_button;
    public TextView notification_status;
    public Spinner period_spinner;
    public Spinner price_spinner;
    @SuppressLint("StaticFieldLeak")
    public static TextView text;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    public Switch notification_switch;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    public Switch sleep_switch;


    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        notification_status = findViewById(R.id.status_text);
        text = findViewById(R.id.last_update_text);
        return_button = findViewById(R.id.return_btn2);
        notification_switch = findViewById(R.id.notify_swtch);
        sleep_switch = findViewById(R.id.notify_swtch2);
        price_spinner = findViewById(R.id.price_spinner);
        period_spinner = findViewById(R.id.period_spinner);

        int period_var = StorageManager.getIntVariable("Period");
        period_spinner.setSelection(period_var);

        int price_var = StorageManager.getIntVariable("Price");
        price_spinner.setSelection(price_var);

        boolean status = StorageManager.getBoolVariable("Notification_status");
        notification_switch.setChecked(status);
        boolean sleep = StorageManager.getBoolVariable("Sleep_mode");
        sleep_switch.setChecked(sleep);
        turnNotificationsText(status);
        Intent service_intent = new Intent(SettingsActivity.this, APIFetchService.class);
        text.setText(StorageManager.getStringVariable("Update"));

        notification_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notification_switch.isChecked()){
                    startService(service_intent);
                }else{
                    stopService(service_intent);
                }
                StorageManager.saveVariable("Notification_status",notification_switch.isChecked());
                turnNotificationsText(notification_switch.isChecked());
            }
        });

        sleep_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StorageManager.saveVariable("Sleep_mode",sleep_switch.isChecked());
            }
        });

        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        period_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                StorageManager.saveVariable("Period", String.valueOf(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing when nothing is selected
            }
        });
        price_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                StorageManager.saveVariable("Price", String.valueOf(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing when nothing is selected
            }
        });

    }

    private void turnNotificationsText(boolean isChecked) {
        if (isChecked){
            notification_status.setText("Активно");
            notification_status.setTextColor(Color.parseColor("#08BC11"));

        }else{
            notification_status.setText("Нективно");
            notification_status.setTextColor(Color.parseColor("#D50000"));
        }
    }
}