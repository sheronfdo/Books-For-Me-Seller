package com.jamith.booksformeseller.model;

import lombok.Data;

import java.util.Date;

@Data
public class BookStock {
    private String bookId;
    private String sellerId;
    private int stock;
    private double price;
    private String condition;
    private Date createdAt;
    private Date updatedAt;
}
