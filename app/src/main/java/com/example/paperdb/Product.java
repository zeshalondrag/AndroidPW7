package com.example.paperdb;

public class Product {
    private String title;
    private String size;
    private double price;
    private String imagePath;

    public Product(String title, String size, double price, String imagePath) {
        this.title = title;
        this.size = size;
        this.price = price;
        this.imagePath = imagePath;
    }

    public String getTitle() {
        return title;
    }

    public String getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }

    public String getImagePath() {
        return imagePath;
    }
}