package com.jamith.booksformeseller.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderItem implements Serializable {
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
    private String status;
}
