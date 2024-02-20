package com.example.bill.feign;

import com.example.bill.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "product", url = "http://localhost:8080/api/product")
public interface APIProduct {

    @GetMapping("/{productId}")
    ProductResponse getProductById(@PathVariable Long productId,
                                   @RequestHeader("Authorization") String accessToken);

    @GetMapping("/{productId}/quantity")
    int getQuantityProduct(@PathVariable Long productId,
                           @RequestHeader("Authorization") String accessToken);

    @PostMapping("/{productId}/quantity")
    void changeQuantity(@PathVariable Long productId,
                                @RequestParam("quantity") int quantity,
                                @RequestHeader("Authorization") String accessToken);
}