package com.example.positioncalculator;

public class Order {

    private double Price;
    private double Amount;

    public Order(double price, double amount){
        this.Amount = amount;
        this.Price = price;
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
}
