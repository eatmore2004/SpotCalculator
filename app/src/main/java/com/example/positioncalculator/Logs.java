package com.example.positioncalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Logs extends AppCompatActivity {

    public Button back_button;
    public Button copy_button;

    public TextView list_view;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        this.back_button = findViewById(R.id.bck_btn);
        this.copy_button = findViewById(R.id.copy_btn);
        this.list_view = findViewById(R.id.list_edt);

        list_view.setText(MainActivity.manager.toString());

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        copy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = list_view.getText().toString();
                if (!message.equals("Пока ничего!")) {
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("LOGS",message);
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(getApplicationContext(), "Скопировано в буфер обмена", Toast.LENGTH_SHORT).show();
                }else Toast.makeText(getApplicationContext(), "Нечего копировать!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}