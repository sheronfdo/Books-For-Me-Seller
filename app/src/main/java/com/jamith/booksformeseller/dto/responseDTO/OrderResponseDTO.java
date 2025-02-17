package com.jamith.booksformeseller.dto.responseDTO;

import java.util.Date;

import lombok.Data;

@Data
public class OrderResponseDTO {
    private String id;
    private Date createdTime;
}
