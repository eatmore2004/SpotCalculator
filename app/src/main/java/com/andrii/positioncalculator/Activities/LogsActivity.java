package com.andrii.positioncalculator.Activities;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andrii.positioncalculator.Adapters.LogsListAdapter;
import com.andrii.positioncalculator.Helpers.Emoji;
import com.andrii.positioncalculator.Helpers.Position;
import com.andrii.positioncalculator.Helpers.StorageManager;
import com.andrii.positioncalculator.Helpers.Utils;
import com.example.positioncalculator.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class LogsActivity extends AppCompatActivity {

    public ImageView back_button;
    public ImageView copy_button;
    public ImageView scan_button;
    public ImageView clr_button;
    public ImageView qr;
    public static TextView stats_box;
    private boolean QRIsShowed = false;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);
        initInterface();
        reloadStats();
        loadNodes(MainActivity.position);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        copy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = MainActivity.position.getHash();
                if (!message.equals("")) {
                    shareWith(message);
                    //copyToClipboard(message); Better to use shareWith()
                }else Toast.makeText(getApplicationContext(), "Нечего копировать!", Toast.LENGTH_SHORT).show();
            }
        });
        clr_button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                MainActivity.ClearEdits();
                Emoji emoji = Emoji.values()[(int)(Math.random()*Emoji.values().length)];
                MainActivity.message_box.setText("\n\t\tВведите значения " + Utils.getEmoji(emoji));
                MainActivity.position.clear();
                finish();
            }
        });
        scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!QRIsShowed){
                    String message = MainActivity.position.getHash();
                    if (!message.equals("")) {
                        generateQR(message);
                        ShowQR(true);
                    }else Toast.makeText(getApplicationContext(), "Нет позиции!", Toast.LENGTH_SHORT).show();
                }else{
                    ShowQR(false);
                }

            }
        });

    }

    public static void reloadStats() {
        stats_box.setText(MainActivity.position.toString());
    }

    private void loadNodes(Position position) {;
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new LogsListAdapter(this,MainActivity.position.journal));
    }

    private void initInterface() {
        this.stats_box = findViewById(R.id.stats_box);
        this.back_button = findViewById(R.id.bck_btn);
        this.copy_button = findViewById(R.id.copy_btn);
        this.scan_button = findViewById(R.id.showqr_btn);
        this.qr = findViewById(R.id.qrcode);
        this.clr_button = findViewById(R.id.clear_btn2);
    }

    @Deprecated
    private void copyToClipboard(String message) {
        // Better to use shareWith()
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("LOGS",message);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(getApplicationContext(), "Скопировано в буфер обмена", Toast.LENGTH_SHORT).show();
    }

    private void shareWith(String message) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String Body = "Поделиться результатом";
        intent.putExtra(Intent.EXTRA_TEXT,message);
        startActivity(Intent.createChooser(intent,Body));
    }

    private void generateQR(String message) {
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(message, BarcodeFormat.QR_CODE,1200,1200);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            qr.setImageBitmap(bitmap);
        }catch (WriterException e){
            Toast.makeText(getApplicationContext(), "Чтото пошло не так", Toast.LENGTH_SHORT).show();
        }
    }

    private void ShowQR(boolean state){
        qr.setVisibility((state) ? View.VISIBLE : View.GONE);
        QRIsShowed = state;
    }

    @Override
    protected void onPause() {
        super.onPause();
        StorageManager.savePosition("current_position",MainActivity.position);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadStats();
    }
}