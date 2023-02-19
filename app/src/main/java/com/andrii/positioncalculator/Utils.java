package com.andrii.positioncalculator;

import java.util.ArrayList;

public class Utils {

    public static ArrayList<Order> getStats(Position position){
        if (position.getHash().equals("")) return null;
        ArrayList<Order> buyed = position.getBuy_orders();
        ArrayList<Order> result = new ArrayList<>();
        for (int i = 0; i < buyed.size(); i++) {
            Order order = buyed.get(i);
            int order_price = order.getPriceInt();
            int index = contains(result,order_price);
            if(index == -1){
                result.add(new Order(order_price,order.getAmount(), order.getAmount()));
            }else{
                double amount = result.get(index).getAmount() + order.getAmount();
                result.set(index,new Order(order_price,amount,amount));
            }
        }
        return result;
    }

    private static int contains(ArrayList<Order> book, int order_price) {
        if (book.isEmpty()) return -1;
        for (int i = 0; i < book.size(); i++) {
            if (book.get(i).getPrice() == order_price) return i;
        }
        return -1;
    }
    public static String getEmoji(Emoji emoji){
        int unicode = 0x1F626;
        switch (emoji){
            case SMILE:
                unicode = 0x1F600; break;
            case THINKING:
                unicode = 0x1F914; break;
            case WINKING:
                unicode = 0x1F609; break;
            case COOL:
                unicode = 0x1F60E; break;
        }
        return new String(Character.toChars(unicode));
    }

}
