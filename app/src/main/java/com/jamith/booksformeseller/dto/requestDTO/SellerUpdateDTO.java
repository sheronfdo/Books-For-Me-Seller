package com.jamith.booksformeseller.dto.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerUpdateDTO {
    private String id;
    private String fullName;
    private String phoneNumber;
    private String companyName;
    private String registrationNumber;
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String imageUrl;
}
