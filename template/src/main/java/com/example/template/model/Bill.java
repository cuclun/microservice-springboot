package com.example.template.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Bill {
    private Long id;

    private String fullName;

    private String phone;

    private String address;

    private Long productId;

    private int quantity;
}
