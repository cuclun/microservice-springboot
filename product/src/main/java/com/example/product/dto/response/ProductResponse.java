package com.example.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private int quantity;
    private Long price;
    private CategoryResponse categoryResponse;
    private boolean deleted;
}
