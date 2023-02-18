package com.andrii.positioncalculator;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.positioncalculator.R;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class AddPosition extends AppCompatActivity {
    private Vibrator vibrator;
    public Button return_button;
    public ImageView load_button;
    public Button paste_button;
    public Button scan_button;
    public TextView hash_edit;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_position);
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        return_button = findViewById(R.id.return_btn);
        load_button = findViewById(R.id.load_btn);
        paste_button = findViewById(R.id.paste_btn);
        scan_button = findViewById(R.id.scan_btn);
        hash_edit = findViewById(R.id.hash_edt);

        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        paste_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                hash_edit.setText(clipboard.getText());
                Analyze(hash_edit.getText().toString());
            }
        });
        load_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Analyze(hash_edit.getText().toString());
            }
        });
        scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanQR();
            }
        });

    }

    private void scanQR() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Наведите на QR code");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }
    private void Analyze(String hash){
        Position new_position = new Position(hash);
        if (new_position.direction == Direction.NONE){
            MainActivity.position = new_position;
            MainActivity.message_box.setText(MainActivity.position.getResponse());
            finish();
        }else{
            hash_edit.setText("");
            Toast.makeText(getApplicationContext(),"Некоректные данные!", Toast.LENGTH_SHORT).show();
        }
    }
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        vibrator.vibrate(100);
        if (result.getContents() != null) {
            Analyze(result.getContents());
        }
        else Toast.makeText(getApplicationContext(),"Некоректный QR!", Toast.LENGTH_SHORT).show();
    });

}