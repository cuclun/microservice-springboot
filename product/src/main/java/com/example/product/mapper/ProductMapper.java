package com.example.product.mapper;

import com.example.product.dto.request.ProductRequest;
import com.example.product.dto.response.ProductResponse;
import com.example.product.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "categoryResponse.id", source = "product.category.id")
    ProductResponse productToProductResponse(Product product);

    @Mapping(target = "category.id", source = "productRequest.categoryId")
    Product productRequestDTOToProduct(ProductRequest productRequest);

}