package com.example.womanstorefinal;

public class Accessories extends Product{

    //Constructor
    public Accessories(String name, double purchasePrice, double sellPrice){
        super(name, purchasePrice, sellPrice);
        discountPrice = sellPrice * 50/100;
    }

    //Methods
    @Override
    public String toString() {
        return "Product : \n" +
                "number= " + number +
                "\nname= " + name +
                "\npurchasePrice=" + purchasePrice +
                "\nsellPrice=" + sellPrice +
                "\ndiscountPrice=" + discountPrice +
                "\nnbItems=" + nbItems+"\n";

    }

    @Override
    public String getType() {
        return "Accessories";
    }

    @Override
    public void applyDiscount(){
        this.onSale = !this.onSale;

    }

}
