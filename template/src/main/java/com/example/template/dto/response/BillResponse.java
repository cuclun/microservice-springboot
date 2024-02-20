package com.example.template.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillResponse {
    private Long id;
    private String fullName;
    private String phone;
    private String address;
    private Long productId;
    private int quantity;
}
