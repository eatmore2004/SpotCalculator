package com.andrii.positioncalculator.Helpers;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Position {

    public Direction direction = Direction.BUY;

    private final ArrayList<Order> journal = new ArrayList<>();
    private final ArrayList<Order> buy_orders = new ArrayList<>();
    private final ArrayList<Order> sell_orders = new ArrayList<>();

    public final String SEPARATOR_O = ":";
    public final String SEPARATOR_P = "/";

    public Position(){
        direction = Direction.NONE;
    }
    public Position(String hash){
        String position_str = Security.decrypt(hash,"Bitcoin");
        if (position_str != null && !position_str.isEmpty()){
            String[] orders = position_str.split("/");
            for (String s : orders) {
                String[] order = s.split(":");
                if (order[0] != null || order[1] != null || order[2] != null || order[3] != null) {
                    try {
                        assert order[1] != null;
                        double price = Double.parseDouble(order[1]);
                        double nofeeamount = Double.parseDouble(order[2]);
                        double amount = Double.parseDouble(order[3]);
                        if (order[0].equals("B")) {
                            addBuy(new Order(price, amount, nofeeamount));
                            direction = Direction.NONE;
                        } else if (order[0].equals("S")) {
                            addSell(new Order(price, amount, nofeeamount));
                            direction = Direction.NONE;
                        } else {
                            direction = Direction.ERROR;
                        }
                    } catch (Exception e1) {
                        direction = Direction.ERROR;
                        break;
                    }
                } else {
                    direction = Direction.ERROR;
                    break;
                }
            }
        }else direction = Direction.ERROR;

    }
    public ArrayList<Order> getSell_orders() {
        return sell_orders;
    }
    public ArrayList<Order> getBuy_orders() {
        return buy_orders;
    }

    public void addBuy(Order order){
        order.setDirection(Direction.BUY);
        journal.add(order);
        buy_orders.add(order);
        direction = Direction.BUY;
    }

    public void addSell(Order order){
        order.setDirection(Direction.SELL);
        journal.add(order);
        sell_orders.add(order);
        direction = Direction.SELL;
    }

    public void clear() {
        buy_orders.removeAll(buy_orders);
        sell_orders.removeAll(sell_orders);
        journal.removeAll(journal);
        direction = Direction.NONE;
    }

    @SuppressLint("DefaultLocale")
    public String getResponse() {

        if (buy_orders.isEmpty() && sell_orders.isEmpty()) return null;
        String message = "\n";
        double av_price_buy = getAveragePrice(buy_orders);
        double buyed_amount = getAmount(buy_orders);

        if (sell_orders.size() > 0 && buy_orders.size() > 0){

            double sold_amount = getAmount(sell_orders);
            double av_price_sell = getAveragePrice(sell_orders);
            double profit = getProfit(sold_amount,av_price_sell,av_price_buy);

            if (sold_amount > buyed_amount) sold_amount = buyed_amount;

            if (profit >= 0) message += "\t\tПрофит: " + String.format("%.5f",profit) + " USDT\n";
            else message += "\t\tУбыток: " + String.format("%.5f",profit) + " USDT\n";

            if ((buyed_amount - sold_amount) != 0) message += "\t\tОсталось в позиции: " + String.format("%.5f",(buyed_amount - sold_amount)) + " BTC\n";

            message += "\t\tСредняя цена продажи: "  + String.format("%.2f",av_price_sell) + "\n";

        }else {
            message = "\n\t\tСредняя цена покупки: " + String.format("%.2f",av_price_buy) + "\n" +
            "\t\tВсего куплено: " + buyed_amount + " BTC";
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
        if (sum == 0 || div == 0){
            return 0;
        }
        return sum / div;
    }


    public Order stepBack() {
        if (direction == Direction.NONE) return new Order(0.0,0.0,0.0);
        if (direction == Direction.BUY){
            direction = Direction.NONE;
            return removeOrderFrom(buy_orders);
        }else{
            direction = Direction.NONE;
            return removeOrderFrom(sell_orders);
        }
    }

    private Order removeOrderFrom(ArrayList<Order> orders) {
        Order order = orders.get(orders.size() - 1);
        orders.remove(orders.size() - 1);
        return order;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        if (buy_orders.isEmpty() && sell_orders.isEmpty()) return "Пока ничего!";

        StringBuilder message = new StringBuilder();
        message.append(">>>>>> Итоги >>>>>>\n\n");
        message.append("\tВсего куплено : ").append(getAmount(buy_orders)).append(" BTC\n");
        message.append("\tВсего продано : ").append(getAmount(sell_orders)).append(" BTC\n");
        message.append("\tОстаеться в позиции : ").append(String.format("%.5f",getAmount(buy_orders) - getAmount(sell_orders))).append(" BTC\n");
        double av_buy = getAveragePrice(buy_orders);
        double av_sell = getAveragePrice(sell_orders);
        message.append("\tСредняя цена покупки: ").append((av_buy <= 0) ? "НЕИЗВЕСТНО" : String.format("%.2f",av_buy)).append(" USDT\n");
        message.append("\tСредняя цена продажи: ").append((av_sell <= 0) ? "НЕИЗВЕСТНО" : String.format("%.2f",av_sell)).append(" USDT\n");
        message.append("\tПрофит: ");
        if (sell_orders.size() > 0 && buy_orders.size() > 0){
            message.append(String.format("%.3f",getProfit(getAmount(sell_orders),av_sell,av_buy))).append(" USDT \n");
        }else message.append("НЕИЗВЕСТНО USDT \n");
        message.append("\n>>>>>> История торгов >>>>>>\n\n");
        for (int i = 0; i < journal.size(); i++) {
            Order order = journal.get(i);
            double price = order.getPrice();
            double amount = order.getAmount();
            String direction = "";
            switch(order.getDirection()){
                case BUY : direction = "BUY"; break;
                case SELL : direction = "SELL"; break;
            }
            if (direction.isEmpty()) return "ERROR";
            message.append("\t").append(direction).append(" : ").append(price).append(" на ").append(amount).append("BTC (≈").append(Math.round(price * amount)).append(" USDT)\n");
        }
        return message.toString();
    }

    public String getHash(){
        StringBuilder hash = new StringBuilder();

        if (buy_orders.isEmpty() && sell_orders.isEmpty()) return "";

        for (int i = 0; i < buy_orders.size(); i++) {
            hash.append("B" + SEPARATOR_O).append(buy_orders.get(i).getPrice()).append(SEPARATOR_O).append(buy_orders.get(i).getInitialAmount()).append(SEPARATOR_O).append(buy_orders.get(i).getAmount()).append(SEPARATOR_P);
        }
        for (int i = 0; i < sell_orders.size(); i++) {
            hash.append("S" + SEPARATOR_O).append(sell_orders.get(i).getPrice()).append(SEPARATOR_O).append(sell_orders.get(i).getInitialAmount()).append(SEPARATOR_O).append(sell_orders.get(i).getAmount()).append(SEPARATOR_P);
        }

        return Security.encrypt(hash.toString(),"Bitcoin");

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != Position.class) return false;

        Position position = (Position) obj;

        if (position.getBuy_orders().size() != buy_orders.size() || position.getSell_orders().size() != sell_orders.size())
            return false;

        ArrayList<Order> b_orders = position.getBuy_orders();
        ArrayList<Order> s_orders = position.getSell_orders();

        for (int i = 0; i < b_orders.size(); i++) {
            Order order1 = buy_orders.get(i);
            Order order2 = b_orders.get(i);
            if (! order1.equals(order2)) return false;
        }

        for (int i = 0; i < s_orders.size(); i++) {
            if (!sell_orders.get(i).equals(s_orders.get(i))) return false;
        }
        return true;
    }
}
