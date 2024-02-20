package com.example.template.FeignClient;

import com.example.template.dto.request.CategoryRequest;
import com.example.template.dto.request.ProductRequest;
import com.example.template.dto.response.CategoryResponse;
import com.example.template.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "product", url = "http://localhost:8080/api/")
public interface ProductAPI {

    @GetMapping(path = "product/")
    Page<ProductResponse> getProduct(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "5") int size,
                                     @RequestParam(defaultValue = "id") String sortBy,
                                     @RequestParam(defaultValue = "DESC") String sortType);

    @GetMapping(path = "category/")
    Page<CategoryResponse> getCategory(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "5") int size,
                                       @RequestParam(defaultValue = "id") String sortBy,
                                       @RequestParam(defaultValue = "DESC") String sortType);

    @GetMapping(path = "category/{categoryId}/products")
    List<ProductResponse> getProductByCategoryId(@PathVariable Long categoryId);

    @GetMapping("product/{productId}")
    ProductResponse getProductById(@PathVariable Long productId);

    @GetMapping(path = "product/search")
    List<ProductResponse> search(@RequestParam("keyword") String keyword);

    @GetMapping(path = "category/{categoryId}")
    CategoryResponse getCategoryById(@PathVariable Long categoryId);

    @PostMapping(path = "category/")
    CategoryRequest addCategory(@RequestParam String name);

    @PostMapping(path = "product/")
    String addProduct(ProductRequest productRequest);

    @PutMapping(path = "category/{categoryId}")
    CategoryResponse updateCategory(@RequestParam String name, @PathVariable Long categoryId);

    @PutMapping(path = "product/{productId}")
    ProductResponse updateProduct(ProductRequest productRequest, @PathVariable Long productId);

    @DeleteMapping(path = "product/{productId}")
    void deleteProduct(@PathVariable Long productId);

    @DeleteMapping(path = "category/{categoryId}")
    void deleteCategory(@PathVariable Long categoryId);
}