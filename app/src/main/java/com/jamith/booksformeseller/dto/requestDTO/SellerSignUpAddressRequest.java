package com.jamith.booksformeseller.dto.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SellerSignUpAddressRequest {
    private String id;
    private String addressStreet;
    private String addressCity;
    private String addressState;
    private String addressPostalCode;
    private String addressCountry;
    private double latitude;
    private double longitude;
}
