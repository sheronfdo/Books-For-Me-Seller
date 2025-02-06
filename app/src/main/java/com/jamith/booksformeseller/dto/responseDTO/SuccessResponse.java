package com.jamith.booksformeseller.dto.responseDTO;

import lombok.Data;

@Data
public class SuccessResponse {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private Object data;
}
