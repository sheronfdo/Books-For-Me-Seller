package com.jamith.booksformeseller.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Language {
    private String id;
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
