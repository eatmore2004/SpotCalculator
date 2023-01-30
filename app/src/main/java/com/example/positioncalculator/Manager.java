package com.example.positioncalculator;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Manager {

    private Direction lastOrder = Direction.BUY;
    private final ArrayList<Order> buy_orders = new ArrayList<>();

    public ArrayList<Order> getBuy_orders() {
        return buy_orders;
    }

    public ArrayList<Order> getSell_orders() {
        return sell_orders;
    }

    private final ArrayList<Order> sell_orders = new ArrayList<>();

    public void addBuy(Order order){
        buy_orders.add(order);
        lastOrder = Direction.BUY;
    }

    public void addSell(Order order){
        sell_orders.add(order);
        lastOrder = Direction.SELL;
    }

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


    public Order stepBack() {
        if (lastOrder == Direction.NONE) return new Order(0.0,0.0,0.0);
        if (lastOrder == Direction.BUY){
            lastOrder = Direction.NONE;
            return removeOrderFrom(buy_orders);
        }else{
            lastOrder = Direction.NONE;
            return removeOrderFrom(sell_orders);
        }
    }

    private Order removeOrderFrom(ArrayList<Order> orders) {
        Order order = orders.get(orders.size() - 1);
        orders.remove(orders.size() - 1);
        return order;
    }

    @NonNull
    @Override
    public String toString() {
        if (buy_orders.isEmpty() && sell_orders.isEmpty()) return "Пока ничего!";

        StringBuilder message = new StringBuilder();

        for (int i = 0; i < buy_orders.size(); i++) {
            message.append("BUY: ").append(buy_orders.get(i).getAmount()).append(" BTC по ").append(buy_orders.get(i).getPrice()).append(" (fee ").append(buy_orders.get(i).getInitialAmount() - buy_orders.get(i).getAmount()).append("USDT)\n");
        }
        for (int i = 0; i < sell_orders.size(); i++) {
            message.append("SELL: ").append(sell_orders.get(i).getAmount()).append(" BTC по ").append(sell_orders.get(i).getPrice()).append(" (fee ").append(sell_orders.get(i).getInitialAmount() - sell_orders.get(i).getAmount()).append("USDT)\n");
        }
        return message.toString();
    }
}
