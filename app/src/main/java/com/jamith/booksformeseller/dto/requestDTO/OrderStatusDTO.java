package com.jamith.booksformeseller.dto.requestDTO;

import com.jamith.booksformeseller.util.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderStatusDTO {
    private String orderId;
    private String orderItemId;
    private String sellerId;
    private OrderStatus paymentStatus;
}
