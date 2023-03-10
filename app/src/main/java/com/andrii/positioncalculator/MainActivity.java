package com.andrii.positioncalculator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.andrii.positioncalculator.Helpers.Emoji;
import com.andrii.positioncalculator.Helpers.Order;
import com.andrii.positioncalculator.Helpers.Position;
import com.andrii.positioncalculator.Helpers.StorageManager;
import com.andrii.positioncalculator.Helpers.Utils;
import com.example.positioncalculator.R;

import java.util.Locale;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    public Button buy_button;
    public Button sell_button;
    public ImageView stats_button;
    public ImageView back_button;
    public ImageView journal_button;
    public ImageView settings_button;
    public ImageView positionload_button;
    public ImageView clr_price_button;
    public ImageView clr_amount_button;
    public ImageView clr_fee_button;

    @SuppressLint("StaticFieldLeak")
    public static EditText price_edit;
    @SuppressLint("StaticFieldLeak")
    public static EditText amount_edit;
    @SuppressLint("StaticFieldLeak")
    public static EditText fee_edit;
    @SuppressLint("StaticFieldLeak")
    public static TextView message_box;

    public static Position position;


    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Paper.init(getApplicationContext());

        initInterface();
        initPosition();

        buy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Order order = getOrder();
                if (order != null){
                    position.addBuy(order);
                    ClearEdits();
                    message_box.setText(position.getResponse());
                }
            }
        });

        sell_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Order order = getOrder();
                if (order != null){
                    position.addSell(order);
                    ClearEdits();
                    message_box.setText(position.getResponse());
                }
            }
        });

        clr_price_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                price_edit.setText("");
            }
        });
        clr_amount_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount_edit.setText("");
            }
        });
        clr_fee_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fee_edit.setText("");
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClearEdits();
                Order last_order = position.stepBack();
                if (last_order.getPrice() <= 0 || last_order.getAmount() <= 0){
                    ClearEdits();
                    Toast.makeText(getApplicationContext(),"Невозможно вернуться на шаг назад!", Toast.LENGTH_SHORT).show();
                }else{
                    price_edit.setText(String.format(Locale.US,"%.2f",last_order.getPrice()));
                    amount_edit.setText(String.format(Locale.US,"%.6f",last_order.getAmount()));
                }
            }
        });
        journal_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LogsActivity.class);
                startActivity(intent);
            }
        });

        positionload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddPositionActivity.class);
                startActivity(intent);
            }
        });
        stats_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!position.getBuy_orders().isEmpty()){
                    Intent intent = new Intent(MainActivity.this, ChartActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Совершите хотя бы одну покупку", Toast.LENGTH_SHORT).show();
                }
            }
        });
        settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initInterface() {
        this.buy_button = findViewById(R.id.buy_btn);
        this.sell_button = findViewById(R.id.sell_btn);
        this.stats_button = findViewById(R.id.stats_btn);
        this.back_button = findViewById(R.id.back_btn);
        this.journal_button = findViewById(R.id.journal_btn);
        this.positionload_button = findViewById(R.id.loadposition_btn);
        price_edit = findViewById(R.id.price_edt);
        amount_edit = findViewById(R.id.amount_edt);
        fee_edit = findViewById(R.id.fee_edt);
        this.clr_price_button = findViewById(R.id.clr_price_btn);
        this.clr_amount_button = findViewById(R.id.clr_amount_btn);
        this.clr_fee_button = findViewById(R.id.clr_fee_btn);
        this.settings_button = findViewById(R.id.settings_btn);
        message_box = findViewById(R.id.message);
    }

    @SuppressLint("SetTextI18n")
    private void initPosition() {
        position = StorageManager.getPosition("current_position");
        String response = position.getResponse();
        if (response == null){
            Emoji emoji = Emoji.values()[(int)(Math.random()*Emoji.values().length)];
            MainActivity.message_box.setText("\n\t\tВведите значения " + Utils.getEmoji(emoji));
        }else message_box.setText(response);
    }

    public static void ClearEdits() {
        price_edit.setText("");
        amount_edit.setText("");
        fee_edit.setText("");
    }

    private Order getOrder(){
        double price, amount, fee = 0;
        String price_text = price_edit.getText().toString();
        String amount_text = amount_edit.getText().toString();
        String fee_text = fee_edit.getText().toString();

        if(!price_text.isEmpty() || !amount_text.isEmpty()){
            try {
                price = Double.parseDouble(price_text);
                amount = Double.parseDouble(amount_text);
                if (!fee_text.isEmpty()) fee = Double.parseDouble(fee_text);
                if (price > 0 && amount > 0) return new Order(price,amount * (1 - 0.01 * fee),amount);
                else Toast.makeText(getApplicationContext(),"Не может = 0!", Toast.LENGTH_LONG).show();
            }catch (Exception e1){
                Toast.makeText(getApplicationContext(),"Некоректные данные!", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext(),"Введите все данные!", Toast.LENGTH_SHORT).show();
        }

        return null;
    }
    @Override
    protected void onPause() {
        super.onPause();
        StorageManager.savePosition("current_position",position);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String response = position.getResponse();
    }
}