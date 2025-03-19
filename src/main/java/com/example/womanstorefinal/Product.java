package com.example.womanstorefinal;

public abstract class Product implements Discount, Comparable<Product> {

    // Static fields
    static int cpt = 0;
    static double capital;
    static double income = 0.0;
    static double cost = 0.0;

    // Instance fields
    int number;
    String name;
    double purchasePrice;
    double sellPrice;
    double discountPrice;
    int nbItems;
    boolean onSale = false;
    private int size = -1;  // Default value indicating "no size"

    // Constructor
    public Product(String name, double purchasePrice, double sellPrice) {
        if (purchasePrice < 0 || sellPrice < 0) {
            throw new IllegalArgumentException("Negative price!");
        }
        this.name = name;
        this.purchasePrice = purchasePrice;
        this.sellPrice = sellPrice;
        this.number = ++cpt;
        this.discountPrice = 0.0;
        this.nbItems = 0;
    }

    // Static getters and setters
    public static int getCpt() { return cpt; }
    public static double getCapital() { return capital; }
    public static void setCapital(double capital) { Product.capital = capital; }
    public static double getIncome() { return income; }
    public static void setIncome(double income) { Product.income = income; }
    public static double getCost() { return cost; }
    public static void setCost(double cost) { Product.cost = cost; }

    // Instance getters and setters
    public int getNumber() { return number; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(double purchasePrice) {
        if (purchasePrice < 0) {
            throw new IllegalArgumentException("Negative price!");
        }
        this.purchasePrice = purchasePrice;
    }
    public double getSellPrice() { return sellPrice; }
    public void setSellPrice(double sellPrice) {
        if (sellPrice < 0) {
            throw new IllegalArgumentException("Negative price!");
        }
        this.sellPrice = sellPrice;
    }
    public double getDiscountPrice() { return discountPrice; }
    public void setDiscountPrice(double discountPrice) { this.discountPrice = discountPrice; }
    public int getNbItems() { return nbItems; }
    public void setNbItems(int nbItems) { this.nbItems = nbItems; }

    // Size-related getters and setters (default value is -1)
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    // Methods for selling and purchasing products
    public void sell(int nbItems) {
        if (this.nbItems < nbItems) {
            throw new IllegalArgumentException("Product Unavailable");
        } else {
            this.nbItems -= nbItems;
            income += (onSale ? nbItems * this.discountPrice : nbItems * this.sellPrice);
        }
    }

    public void purchase(int nbItems) {
        this.nbItems += nbItems;
        cost += nbItems * this.purchasePrice;
    }

    @Override
    public int compareTo(Product o) {
        return Double.compare(this.sellPrice, o.sellPrice);
    }

    @Override
    public String toString() {
        return "Product{" +
                "number=" + number +
                ", name='" + name + '\'' +
                ", sellPrice=" + sellPrice +
                ", nbItems=" + nbItems +
                '}';
    }

    public abstract String getType();

    public void setType(String value) {
    }
}
