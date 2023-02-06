package com.andrii.positioncalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.positioncalculator.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class Logs extends AppCompatActivity {

    public Button back_button;
    public Button copy_button;
    public Button scan_button;

    public ImageView qr;

    public TextView list_view;

    private boolean QRIsShowed = false;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        this.back_button = findViewById(R.id.bck_btn);
        this.copy_button = findViewById(R.id.copy_btn);
        this.list_view = findViewById(R.id.list_edt);
        this.scan_button = findViewById(R.id.showqr_btn);
        this.qr = findViewById(R.id.qrcode);
        list_view.setText(MainActivity.position.toString());

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
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("LOGS",message);
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(getApplicationContext(), "Скопировано в буфер обмена", Toast.LENGTH_SHORT).show();
                }else Toast.makeText(getApplicationContext(), "Нечего копировать!", Toast.LENGTH_SHORT).show();
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
}