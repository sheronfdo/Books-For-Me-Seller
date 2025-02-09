package com.jamith.booksformeseller.dto.requestDTO;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddNewBookStockDTO {
    private String bookId;
    private String sellerId;
    private int stock;
    private double price;
    private String condition;
}
