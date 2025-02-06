package com.jamith.booksformeseller.dto.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SellerSignUpBrRequest {
    private String id;
    private String companyName;
    private String businessRegistrationNumber;
    private String brDocDownUrl;
}
