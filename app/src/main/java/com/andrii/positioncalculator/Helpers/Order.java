package com.andrii.positioncalculator.Helpers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.UUID;

public class Order {

    private final double Price;
    private final double Amount;
    public UUID ID;
    private final double NoFeeAmount;

    private Direction direction;

    public Order(double price, double amount, double noFeeAmount){
        this.Amount = amount;
        this.Price = price;
        this.NoFeeAmount = noFeeAmount;
        ID = UUID.randomUUID();
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public double getPrice() {
        return Price;
    }

    public double getAmount() {
        return Amount;
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
    @NonNull
    @Override
    public String toString() {
        return "Цена "+ getPrice() + " / Кол-во "+ getAmount() +" (" + Math.round(getPrice() * getAmount())+"$)";
    }
}
