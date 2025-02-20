package com.jamith.booksformeseller.model;

import java.util.Date;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
    private Map<String, String> address;
    private Map<String, String> businessDetails;
    private Date createdAt;
    private String email;
    private String fcmToken;
    private String fullNameOrRepresentative;
    private String imageUrl;
    private String phoneNumber;
    private String role;
    private String sellerId;
    private String sellerType;
    private Date updatedAt;
    private boolean verified;
}
