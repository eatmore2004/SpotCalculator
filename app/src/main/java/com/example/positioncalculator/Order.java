package com.example.positioncalculator;

public class Order {

    private double Price;
    private double Amount;
    private double NoFeeAmount;

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
}
