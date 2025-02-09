package com.jamith.booksformeseller.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
public class Category {
    private String id;
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
