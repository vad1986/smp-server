package com.ads.dataObjects;

import java.util.Objects;

public class Product implements Comparable<Product> {
    private String title;
    private String category;
    private double price;
    private int serial;

    public Product(String title, String category, double price, int serial) {
        this.title = title;
        this.category = category;
        this.price = price;
        this.serial = serial;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getSerial() {
        return serial;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }

    @Override
    public int compareTo(Product other) {
        if(this.getPrice() < other.getPrice()) {
            return 1;
        } else if (this.getPrice() > other.getPrice()) {
            return -1;
        } else {
            return 0;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return this.getTitle().equals(product.getTitle()) &&
                Double.compare(this.getPrice(),product.getPrice())==0 &&
                getCategory().equals(product.getCategory());
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, price,category);
    }

    @Override
    public String toString() {
        return "Product="+getTitle()+"{" +
                "title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", serial=" + serial +
                '}';
    }
}
