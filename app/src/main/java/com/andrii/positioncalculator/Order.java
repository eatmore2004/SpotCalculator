package com.andrii.positioncalculator;

import androidx.annotation.Nullable;

public class Order {

    private double Price;
    private double Amount;
    private final double NoFeeAmount;

    public Order(double price, double amount, double noFeeAmount){
        this.Amount = amount;
        this.Price = price;
        this.NoFeeAmount = noFeeAmount;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double amount) {
        Amount = amount;
    }

    public double getInitialAmount() {
        return NoFeeAmount;
    }

    public int getPriceInt(){
        return (int) Math.round(Price / 100) * 100;
    }
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj != null && !(obj.getClass() == Order.class)) return false;
        Order order = (Order) obj;
        return (order != null && order.getAmount() == Amount && order.getPrice() == Price && order.getInitialAmount() == NoFeeAmount);
    }
}
