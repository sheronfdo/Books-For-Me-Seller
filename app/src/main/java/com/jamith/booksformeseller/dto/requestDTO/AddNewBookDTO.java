package com.jamith.booksformeseller.dto.requestDTO;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class AddNewBookDTO {
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
}
