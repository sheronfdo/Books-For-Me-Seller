package com.jamith.booksformeseller.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class Book implements Serializable {
    private String bookId;
    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private String category;
    private String description;
    private String coverImage;
    private int publicationYear;
    private String language;
    private List<String> tags;
    private String createdUser;
    private Date createdAt;
    private Date updatedAt;

    public Book(String title, String author, String isbn, String category) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
    }
}