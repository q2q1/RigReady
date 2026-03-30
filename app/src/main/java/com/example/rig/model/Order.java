package com.example.rig.model;

import com.google.firebase.firestore.DocumentSnapshot;

public class Order {
    public String id;
    public String buyerId;
    public String sellerId;
    public String productId;
    public String productName;
    public double price;

    public String status;
    public String paymentMethod;
    public String shippingAddress;

    public Order() {
    }

    public static Order from(DocumentSnapshot doc) {
        Order o = new Order();
        o.id = doc.getId();
        o.buyerId = doc.getString("buyerId");
        o.sellerId = doc.getString("sellerId");
        o.productId = doc.getString("productId");
        o.productName = doc.getString("productName");
        Double price = doc.getDouble("price");
        o.price = price != null ? price : 0.0;
        o.status = doc.getString("status");
        o.paymentMethod = doc.getString("paymentMethod");
        o.shippingAddress = doc.getString("shippingAddress");
        return o;
    }
}

