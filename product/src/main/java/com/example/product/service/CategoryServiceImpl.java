package com.example.product.service;

import com.example.product.dto.request.CategoryRequest;
import com.example.product.dto.response.CategoryResponse;
import com.example.product.mapper.CategoryMapper;
import com.example.product.model.Category;
import com.example.product.repository.CategoryRepository;
import com.example.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl {

    private final CategoryMapper categoryMapper;

    private final CategoryRepository categoryRepository;

    private final ProductRepository productRepository;

    public boolean isExists(String name) {
        return categoryRepository.existsByName(name);
    }

    public Page<Category> findAll(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);

        return categories;
    }

    public CategoryResponse findById(Long id) {
        return categoryMapper.categoryToCategoryResponse(categoryRepository.findById(id).orElse(null));
    }

    public CategoryResponse findNameById(Long id) {
        return categoryMapper.categoryToCategoryResponse(categoryRepository.findById(id).orElse(null));
    }

    public Category save(CategoryRequest categoryRequest) {
        Category category = categoryMapper.categoryRequestDTOToCategory(categoryRequest);

        return categoryRepository.save(category);
    }

    public CategoryResponse update(Long categoryId, CategoryRequest categoryRequest) {
        Category existingCategory = categoryRepository.findById(categoryId).get();

        existingCategory.setName(categoryRequest.getName());

        return CategoryMapper.INSTANCE.categoryToCategoryResponse(categoryRepository.save(existingCategory));
    }

    public void delete(Long categoryId) {
        if (productRepository.countProductsByCategory_Id(categoryId) > 0) {
            throw new RuntimeException("Còn sản phẩm trong danh mục");
        }
        categoryRepository.deleteById(categoryId);
    }
}
