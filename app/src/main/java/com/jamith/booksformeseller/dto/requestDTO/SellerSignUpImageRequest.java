package com.jamith.booksformeseller.dto.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SellerSignUpImageRequest {
    private String id;
    private String imageUrl;
}
