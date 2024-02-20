package com.example.product.controller;

import com.example.product.dto.request.ProductRequest;
import com.example.product.dto.response.ProductResponse;
import com.example.product.model.Category;
import com.example.product.model.Product;
import com.example.product.service.ProductServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductServiceImpl productService;

    @GetMapping(path = {"/", ""})
    public ResponseEntity<?> product(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") String size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortType,
            @RequestParam(defaultValue = "") String keyword) {
        Pageable pageable = size.equals("all")
                ? PageRequest.of(0, Integer.MAX_VALUE)
                : PageRequest.of(page - 1, Integer.parseInt(size), Sort.Direction.fromString(sortType),
                sortBy);
        Page<ProductResponse> products = productService.search(keyword, pageable);

        return ResponseEntity.ok(products);
    }

//    @GetMapping(path ="/search")
//    public ResponseEntity<?> searchProduct(@Param("keyword") String keyword) {
//        return ResponseEntity.ok(productService.search(keyword));
//    }

    @GetMapping(path = {"/{productId}/quantity/", "/{productId}/quantity"})
    public int getQuantity(@PathVariable Long productId) {
        return productService.findQuantityById(productId);
    }

    @PostMapping(path = "/{productId}/quantity")
    public ResponseEntity<?> changeQuantity(@PathVariable Long productId, @Param("quantity") int quantity) {
        productService.changeQuantity(productId, quantity);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(path = {"/{productId}", "/{productId}/"})
    public ResponseEntity<?> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.findById(productId));
    }

    @PostMapping(path = {"/", ""})
    public ResponseEntity<?> addProduct(@RequestBody ProductRequest productRequest) {
        if (productService.isExists(productRequest.getName())) {
            return ResponseEntity.badRequest().body(Map.of("errorCode", 400,
                    "message", "Sản phẩm đã tồn tại trong hệ thông"));
        }
        try {
            ProductResponse product = productService.save(productRequest);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errorCode", 400,
                    "message", e.getMessage()));
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> editProduct(@PathVariable Long productId,
                                         @RequestBody ProductRequest productRequest) {
        try {
            return ResponseEntity.ok(productService.update(productId, productRequest));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errorCode", 400,
                    "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        try {
            productService.delete(productId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errorCode", 400,
                    "message", e.getMessage()));
        }
    }
}
