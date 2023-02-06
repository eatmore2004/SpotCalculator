package com.example.positioncalculator;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public Button buy_button;
    public Button sell_button;
    public Button clr_button;
    public Button back_button;
    public Button journal_button;
    public Button positionload_button;
    public EditText price_edit;
    public EditText amount_edit;
    public EditText fee_edit;
    public static TextView message_box;

    public static Position position = new Position();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.buy_button = findViewById(R.id.buy_btn);
        this.sell_button = findViewById(R.id.sell_btn);
        this.clr_button = findViewById(R.id.clear_btn);
        this.back_button = findViewById(R.id.back_btn);
        this.journal_button = findViewById(R.id.journal_btn);
        this.positionload_button = findViewById(R.id.loadposition_btn);
        this.price_edit = findViewById(R.id.price_edt);
        this.amount_edit = findViewById(R.id.amount_edt);
        this.fee_edit = findViewById(R.id.fee_edt);

        message_box = findViewById(R.id.message);

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

        clr_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClearEdits();
                position.clear();
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClearEdits();
                Order last_order = position.stepBack();
                price_edit.setText(String.format(Locale.US,"%.2f",last_order.getPrice()));
                amount_edit.setText(String.format(Locale.US,"%.6f",last_order.getAmount()));
            }
        });
        journal_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Logs.class);
                startActivity(intent);
            }
        });

        positionload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AddPosition.class);
                startActivity(intent);
            }
        });

    }

    public void ClearEdits() {
        price_edit.setText("");
        amount_edit.setText("");
        fee_edit.setText("");
        message_box.setText("");
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
                return new Order(price,amount * (1 - 0.01 * fee),amount);
            }catch (Exception e1){
                Toast.makeText(getApplicationContext(),"Некоректные данные!", Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(getApplicationContext(),"Введите все данные!", Toast.LENGTH_SHORT).show();
        }

        return null;
    }



}