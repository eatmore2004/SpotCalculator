package com.example.positioncalculator;

import android.annotation.SuppressLint;

import java.util.ArrayList;

public class Manager {

    public ArrayList<Order> buy_orders = new ArrayList<>();
    public ArrayList<Order> sell_orders = new ArrayList<>();

    public void clear() {
        buy_orders.removeAll(buy_orders);
        sell_orders.removeAll(sell_orders);
    }

    @SuppressLint("DefaultLocale")
    public String getResponse() {

        String message = "";
        double av_price_buy = getAveragePrice(buy_orders);
        double buyed_amount = getAmount(buy_orders);

        if (sell_orders.size() > 0){

            double sold_amount = getAmount(sell_orders);
            double av_price_sell = getAveragePrice(sell_orders);
            double profit = getProfit(sold_amount,av_price_sell,av_price_buy);

            if (sold_amount > buyed_amount) sold_amount = buyed_amount;

            if (profit >= 0) message += "Профит: " + String.format("%.5f",profit) + " USDT\n";
            else message += "Убыток: " + String.format("%.5f",profit) + " USDT\n";

            if ((buyed_amount - sold_amount) != 0) message += "Осталось в позиции: " + String.format("%.5f",(buyed_amount - sold_amount)) + " BTC\n";

            message += "Средняя цена продажи: "  + String.format("%.2f",av_price_sell) + "\n";

        }else {
            message = "Средняя цена покупки: " + String.format("%.2f",av_price_buy) + "\n" +
            "Всего куплено: " + buyed_amount + " BTC";
        }

        return message;
    }

    private double getAmount(ArrayList<Order> orders){
        double sum = 0;
        for (int i = 0; i < orders.size(); i++) {
            sum += orders.get(i).getAmount();
        }
        return sum;
    }
    private double getProfit(double amount, double av_price_sell,double av_price_buy){
        return amount * (av_price_sell - av_price_buy);
    }
    private double getAveragePrice(ArrayList<Order> orders) {
        double sum = 0, div = 0;

        for (int i = 0; i < orders.size(); i++) {
            sum += orders.get(i).getPrice() * orders.get(i).getAmount();
            div += orders.get(i).getAmount();
        }

        return sum / div;
    }
}
