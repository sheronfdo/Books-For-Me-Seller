package com.jamith.booksformeseller.util;

public enum OrderStatus {
    // Order Placement
    ORDER_CONFIRMED,
    ORDER_APPROVED,

    // Order Processing and Fulfillment
    PROCESSING,
    SHIPPED,
    DELIVERED,
    ORDER_COMPLETED,

    // Order Cancellations and Failures
    ORDER_CANCELLED,
    ORDER_REJECTED,
    ORDER_FAILED,

    // Payment Statuses
    PAYMENT_PENDING,
    PAYMENT_STATUS_APPROVED,
    PAYMENT_STATUS_REJECTED
}
