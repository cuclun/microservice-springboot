package com.example.product.service;

import com.example.product.dto.request.ProductRequest;
import com.example.product.dto.response.ProductResponse;
import com.example.product.mapper.ProductMapper;
import com.example.product.model.Category;
import com.example.product.model.Product;
import com.example.product.repository.CategoryRepository;
import com.example.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl {

    private final ProductMapper productMapper;

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    public boolean isExists(String name) {
        return productRepository.existsByName(name);
    }

    public Page<ProductResponse> findAll(Pageable pageable) {
        Page<Product> products = productRepository.findAllByDeletedIsFalse(pageable);
        Page<ProductResponse> prdRes = products.map(product -> productMapper.productToProductResponse(product));
        return prdRes;
    }

    public ProductResponse findById(Long productId) {
        return productMapper.productToProductResponse(productRepository.findById(productId).orElse(null));
    }

    public int findQuantityById(Long productId){
        ProductResponse productResponse =
                productMapper.productToProductResponse(productRepository.findById(productId).orElse(null));
        return productResponse.getQuantity();
    }

    public void changeQuantity(Long productId, int quantity){
        Product product = productRepository.findById(productId).orElse(null);

        if (product!=null) {
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);
        }

    }

    public List<ProductResponse> findAllByCategoryId(Long categoryId) {
        List<Product> products = productRepository.findAllByCategory_IdAndDeletedIsFalse(categoryId);

        List<ProductResponse> productResponses = new ArrayList<>();

        for (Product product : products) {
            ProductResponse productResponse = findById(product.getId());
            productResponses.add(productResponse);
        }

        return productResponses;
    }

    public Page<ProductResponse> search(String keyword, Pageable pageable) {
        Page<Product> products = productRepository.findAllByNameContaining(keyword, pageable);
        Page<ProductResponse> productResponses = products.map(productMapper::productToProductResponse);

        return productResponses;
    }

    public ProductResponse save(ProductRequest productRequest) {
        Product product = productMapper.productRequestDTOToProduct(productRequest);
        Category category = categoryRepository.findById(productRequest.getCategoryId()).orElse(null);
        if (category == null) {
            throw new RuntimeException("Không có danh mục");
        }
        product.setCategory(category);
        ProductResponse productResponse = ProductMapper.INSTANCE.productToProductResponse(productRepository.save(product));
        return productResponse;
    }

    public ProductResponse update(Long productId, ProductRequest productRequest) {
        Product existingProduct = productRepository.findById(productId).orElse(null);
        if (existingProduct == null) {
            throw new RuntimeException("Sản phẩm này không có trong hệ thống");
        } else if (productRepository.existsByNameAndIdIsNot(productRequest.getName(), productId)) {
            throw new RuntimeException("Sản phẩm này đã tồn tại");
        } else {
            Product product = productMapper.productRequestDTOToProduct(productRequest);
            product.setId(existingProduct.getId());
            Category category = categoryRepository.findById(productRequest.getCategoryId()).orElse(null);
            if (category == null) {
                throw new RuntimeException("Không có danh mục");
            }
            product.setCategory(category);
            ProductResponse productResponse = ProductMapper.INSTANCE.productToProductResponse(productRepository.save(product));
            return productResponse;
        }
    }

    public void delete(Long productId) {
        Product existingProduct = productRepository.findById(productId).orElse(null);

        if (existingProduct != null) {
            existingProduct.setDeleted(true);
            productRepository.save(existingProduct);
        } else {
            throw new RuntimeException("Sản phẩm không tồn tại");
        }
    }

}
