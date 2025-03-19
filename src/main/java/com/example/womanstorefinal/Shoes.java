package com.example.womanstorefinal;

public class Shoes extends Product{

    //Attributes
    int shoeSize;

    //Constructor
    public Shoes(String name,double purchasePrice, double sellPrice, int shoeSize){
        super(name,purchasePrice,sellPrice);
        this.shoeSize = shoeSize;
        discountPrice = sellPrice * 20/100;
    }

    //Get Set
    @Override
    public int getSize(){
        return shoeSize;
    }
    public void setShoeSize(int shoeSize){
        this.shoeSize = shoeSize;
    }

    @Override
    public String getType() {
        return "Shoes";
    }

    //Methods
    @Override
    public String toString() {
        return "Product : \n" +
                "number= " + number +
                "\nname= " + name +
                "\nsize= " + shoeSize +
                "\nsellPrice=" + sellPrice +
                "\ndiscountPrice=" + discountPrice +
                "\nnbItems=" + nbItems+"\n";

    }
    @Override
    public void applyDiscount(){
        this.onSale = !this.onSale;

    }

}
