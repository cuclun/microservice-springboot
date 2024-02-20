package com.example.bill.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Product {
    private Long id;

    private String name;

    private Long categoryId;

    private Long price;

    private int quantity;

    private boolean deleted;
}
