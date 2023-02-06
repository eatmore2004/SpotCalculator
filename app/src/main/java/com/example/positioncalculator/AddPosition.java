package com.example.positioncalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AddPosition extends AppCompatActivity {

    public Button return_button;
    public Button load_button;
    public Button paste_button;
    public Button clear_button;

    public TextView hash_edit;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_position);

        clear_button = findViewById(R.id.clear2_btn);
        return_button = findViewById(R.id.return_btn);
        load_button = findViewById(R.id.load_btn);
        paste_button = findViewById(R.id.paste_btn);

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
            }
        });
        load_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Position new_position = new Position(hash_edit.getText().toString());
                if (new_position.direction == Direction.NONE){
                    MainActivity.position = new_position;
                    MainActivity.message_box.setText(MainActivity.position.getResponse());
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"Некоректные данные!", Toast.LENGTH_LONG).show();
                }
            }
        });
        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hash_edit.setText("");
            }
        });

    }
}