package com.jamith.booksformeseller.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class Seller {
    private String sellerType;
    private String fullNameOrRepresentative;
    private String email;
    private String passwordHash;
    private String phoneNumber;
    private String addressStreet;
    private String addressCity;
    private String addressState;
    private String addressPostalCode;
    private String addressCountry;
    private String companyName;
    private String businessRegistrationNumber;
}
