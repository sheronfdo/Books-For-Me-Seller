package com.jamith.booksformeseller.dto.responseDTO;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class SellerSignUpResponseDTO {
    private String id;
    private Date createdTime;
}
