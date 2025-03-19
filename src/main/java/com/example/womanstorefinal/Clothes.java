package com.example.womanstorefinal;

public class Clothes extends Product {

    int size;

    public Clothes(String name, double purchasePrice, double sellPrice) {
        super(name, purchasePrice, sellPrice);
        discountPrice = sellPrice * 30 / 100;
    }

    @Override
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String getType() {
        return "Clothes";
    }

    @Override
    public String toString() {
        return "Product : \n" +
                "number= " + number +
                "\nname= " + name +
                "\nsize= " + size +
                "\nsellPrice= " + sellPrice +
                "\ndiscountPrice= " + discountPrice +
                "\nnbItems= " + nbItems + "\n";
    }

    @Override
    public void applyDiscount() {
        this.onSale = !this.onSale;
    }
}
