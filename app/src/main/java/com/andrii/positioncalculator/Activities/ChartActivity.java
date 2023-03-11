package com.andrii.positioncalculator.Activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.andrii.positioncalculator.Helpers.Direction;
import com.andrii.positioncalculator.Helpers.Order;
import com.andrii.positioncalculator.Helpers.Utils;
import com.example.positioncalculator.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity {
    PieChart pieChart;
    Direction chart_direction;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    public Switch chart_switch;
    Button back_button;
    private ArrayList<BarEntry> barArrayList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        pieChart = findViewById(R.id.chart);
        chart_switch = findViewById(R.id.switch_chart);
        chart_switch.setChecked(false);
        chart_direction = Direction.BUY;
        setupPieChart();
        loadPieChartData();
        back_button = findViewById(R.id.bck2_btn);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        chart_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chart_direction = chart_switch.isChecked() ? Direction.SELL : Direction.BUY;
                loadPieChartData();
            }
        });

    }
    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Структура позиции");
        pieChart.setCenterTextSize(24);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setEnabled(false);
    }

    private void loadPieChartData() {
        ArrayList<PieEntry> entries = getData();
        ArrayList<Integer> colors = new ArrayList<>();
        for (int color: ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }
        for (int color: ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }
        PieDataSet dataSet = new PieDataSet(entries, "Структура покупок");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.animateY(1000, Easing.EaseInOutQuad);
    }
    @NonNull
    private ArrayList<PieEntry> getData(){
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Order> orders = Utils.getStatsByPosition(MainActivity.position, chart_direction);
        if (orders != null) {
            for (int i = 0; i < orders.size(); i++) {
                float amount = (float)orders.get(i).getAmount();
                entries.add(new PieEntry(amount, String.valueOf(orders.get(i).getPriceInt())));
            }
        }
        return entries;
    }

}