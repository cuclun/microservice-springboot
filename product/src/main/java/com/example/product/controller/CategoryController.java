package com.example.product.controller;

import com.example.product.dto.request.CategoryRequest;
import com.example.product.dto.response.CategoryResponse;
import com.example.product.model.Category;
import com.example.product.service.CategoryServiceImpl;
import com.example.product.service.ProductServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final ProductServiceImpl productService;

    private final CategoryServiceImpl categoryService;

    @GetMapping(path = {"/", ""})
    public ResponseEntity<?> getCategory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") String size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortType
    ) {
        Pageable pageable = size.equals("all")
                ? PageRequest.of(0, Integer.MAX_VALUE)
                : PageRequest.of(page - 1, Integer.parseInt(size), Sort.Direction.fromString(sortType), sortBy);
        Page<Category> categories = categoryService.findAll(pageable);

        return ResponseEntity.ok(categories);
    }

    @GetMapping(path = {"/{categoryId}", "/{categoryId}/"})
    public ResponseEntity<?> findByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.findById(categoryId));
    }

    @GetMapping(path = {"/{categoryId}/products", "/{categoryId}/products/"})
    public ResponseEntity<?> getProductByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.findAllByCategoryId(categoryId));
    }

    @PostMapping(path = {"/", ""})
    public ResponseEntity<?> addCategory(@RequestBody CategoryRequest categoryRequest) {
        if (categoryService.isExists(categoryRequest.getName())) {
            return ResponseEntity.badRequest().body(Map.of("errorCode", 400, "message", "Danh mục đã tồn tại trong hệ thông"));
        }
        Category category = categoryService.save(categoryRequest);

        return ResponseEntity.ok(category);
    }

    @PutMapping(path = {"/{categoryId}/", "/{categoryId}"})
    public ResponseEntity<?> editCategory(@PathVariable Long categoryId, @RequestBody CategoryRequest categoryRequest) {
        try {
            CategoryResponse categoryResponse = categoryService.findById(categoryId);

            if (categoryResponse.getName().equals(categoryRequest.getName())) {
                return ResponseEntity.badRequest().body(Map.of("errorCode", 400, "message", "Dữ liệu không có gì thay đổi"));
            } else if (categoryService.isExists(categoryRequest.getName())) {
                return ResponseEntity.badRequest().body(Map.of("errorCode", 400, "message", "Danh mục đã tồn tại trong hệ thông"));
            }
            return ResponseEntity.ok(categoryService.update(categoryId, categoryRequest));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errorCode", 404, "message", "Danh mục không tồn tại"));
        }
    }

    @DeleteMapping(path = {"/{categoryId}/", "/{categoryId}"})
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId) {
        if (categoryService.findById(categoryId) == null) {
            return ResponseEntity.badRequest().body(Map.of("errorCode", 404, "message", "Danh mục không tồn tại"));
        }
        try {
            categoryService.delete(categoryId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errorCode", 500, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("errorCode", 500, "message", "Có lỗi khi xóa danh mục"));
        }
    }
}
