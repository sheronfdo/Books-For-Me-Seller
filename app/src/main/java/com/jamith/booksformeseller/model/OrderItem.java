package com.jamith.booksformeseller.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderItem {
    private String orderItemId;
    private String bookId;
    private String bookStockId;
    private String cartItemId;
    private String imageUrl;
    private String orderId;
    private double price;
    private int quantity;
    private String sellerId;
    private String title;
}
