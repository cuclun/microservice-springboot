package com.example.template.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillRequest {
    private String fullName;
    private String phone;
    private String address;
    private Long productId;
    private int quantity;
}
