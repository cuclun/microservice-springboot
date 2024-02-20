package com.example.bill.dto.response;

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
    private ProductResponse productResponse;
    private int quantity;
}
