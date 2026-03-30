package com.example.rig.model;

import com.google.firebase.firestore.DocumentSnapshot;

public class Product {
    public String id;
    public String sellerId;
    public String name;
    public String description;
    public double price;
    public String brand;
    public String imageUrl;



    public String status;

    public Product() {
    }

    public static Product from(DocumentSnapshot doc) {
        Product p = new Product();
        p.id = doc.getId();
        p.sellerId = doc.getString("sellerId");
        p.name = doc.getString("name");
        p.description = doc.getString("description");
        Double price = doc.getDouble("price");
        p.price = price != null ? price : 0.0;
        p.brand = doc.getString("brand");
        p.imageUrl = doc.getString("imageUrl");
        p.status = doc.getString("status");
        return p;
    }
}

