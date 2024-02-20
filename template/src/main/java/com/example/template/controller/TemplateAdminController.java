package com.example.template.controller;

import com.example.template.FeignClient.BillAPI;
import com.example.template.FeignClient.ProductAPI;
import com.example.template.dto.request.CategoryRequest;
import com.example.template.dto.request.ProductRequest;
import com.example.template.dto.response.BillResponse;
import com.example.template.dto.response.CategoryResponse;
import com.example.template.dto.response.ProductResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class TemplateAdminController {
    private final ProductAPI productAPI;

    private final BillAPI billAPI;

    @GetMapping({"admin", "admin/"})
    public String admin() {
        return "Admin";
    }

    @GetMapping({"category", "category/"})
    public String category(Model model, @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "5") int size,
                           @RequestParam(defaultValue = "id") String sortBy,
                           @RequestParam(defaultValue = "DESC") String sortType) {
        Page<CategoryResponse> categoryResponses = productAPI.getCategory(page, size, sortBy, sortType);
        model.addAttribute("categories", categoryResponses);
        return "Category";
    }

    @GetMapping({"product", "product/"})
    public String product(Model model, @RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "5") int size,
                          @RequestParam(defaultValue = "id") String sortBy,
                          @RequestParam(defaultValue = "DESC") String sortType) {

        Page<ProductResponse> productResponses = productAPI.getProduct(page, size, sortBy, sortType);

        model.addAttribute("currentPage", productResponses.getPageable().getPageNumber());
        model.addAttribute("totalPage", productResponses.getTotalPages());
        model.addAttribute("products", productResponses.getContent());

        return "Product";
    }

    @GetMapping({"bill", "bill/"})
    public String bill(Model model, @RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "5") int size,
                       @RequestParam(defaultValue = "id") String sortBy,
                       @RequestParam(defaultValue = "DESC") String sortType) {

        Page<BillResponse> billResponses = billAPI.getBill(page, size, sortBy, sortType);

        model.addAttribute("bills", billResponses);

        return "Bill";
    }

    @GetMapping({"category/addCategory/", "category/addCategory"})
    public String addCategoryPage(Model model) {
        CategoryRequest categoryForm = new CategoryRequest();
        model.addAttribute("categoryForm", categoryForm);
        return "AddCategory";
    }

    @GetMapping("/category/update/{categoryId}")
    public String updateCategoryPage(Model model, @PathVariable Long categoryId) {
        CategoryResponse categoryResponse = productAPI.getCategoryById(categoryId);
        CategoryRequest categoryForm = new CategoryRequest(categoryId, categoryResponse.getName());
        model.addAttribute("categoryForm", categoryForm);
        return "AddCategory";
    }

    @GetMapping({"product/addProduct"})
    public String addProductPage(Model model, @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "5") int size,
                                 @RequestParam(defaultValue = "id") String sortBy,
                                 @RequestParam(defaultValue = "DESC") String sortType) {
        Page<CategoryResponse> categoryResponses = productAPI.getCategory(page, size, sortBy, sortType);
        ProductRequest productForm = new ProductRequest();
        model.addAttribute("productForm", productForm);
        model.addAttribute("categories", categoryResponses);
        return "AddProduct";
    }

    @GetMapping({"/product/update/{productId}"})
    public String updateProductPage(Model model, @PathVariable Long productId,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "5") int size,
                                    @RequestParam(defaultValue = "id") String sortBy,
                                    @RequestParam(defaultValue = "DESC") String sortType) {
        Page<CategoryResponse> categoryResponses = productAPI.getCategory(page, size, sortBy, sortType);
        ProductResponse productResponse = productAPI.getProductById(productId);
        ProductRequest productForm = new ProductRequest(productResponse.getId(), productResponse.getName(),
                productResponse.getQuantity(), productResponse.getPrice(), productResponse.getCategoryId());
        model.addAttribute("productForm", productForm);
        model.addAttribute("categories", categoryResponses);
        return "AddProduct";
    }

    @PostMapping({"/category/addCategory"})
    public String addCategory(@ModelAttribute("categoryForm") CategoryRequest categoryForm, Model model,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "5") int size,
                              @RequestParam(defaultValue = "id") String sortBy,
                              @RequestParam(defaultValue = "DESC") String sortType) throws JsonProcessingException {
        try {
            productAPI.addCategory(categoryForm.getName());
            Page<CategoryResponse> categoryResponses = productAPI.getCategory(page, size, sortBy, sortType);

            model.addAttribute("message", "Thêm danh mục thành công");
            model.addAttribute("categories", categoryResponses);
            return "Category";
        } catch (FeignException e) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(e.contentUTF8());
            model.addAttribute("message", jsonNode.get("message").asText());

            CategoryResponse categoryResponse = new CategoryResponse();

            model.addAttribute("product", categoryResponse);
            return "AddCategory";
        }
    }

    @PostMapping({"/category/update/{categoryId}"})
    public String updateCategory(@ModelAttribute("categoryForm") CategoryRequest categoryForm, Model model,
                                 @PathVariable Long categoryId,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "5") int size,
                                 @RequestParam(defaultValue = "id") String sortBy,
                                 @RequestParam(defaultValue = "DESC") String sortType) throws JsonProcessingException {
        try {
            productAPI.updateCategory(categoryForm.getName(), categoryId);
            Page<CategoryResponse> categoryResponses = productAPI.getCategory(page, size, sortBy, sortType);

            model.addAttribute("message", "Cập nhật thành công");
            model.addAttribute("categories", categoryResponses);
            return "Category";
        } catch (FeignException e) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(e.contentUTF8());
            model.addAttribute("message", jsonNode.get("message").asText());
            model.addAttribute("categoryForm", categoryForm);
            return "AddCategory";
        }
    }

    @PostMapping("/product/addProduct")
    public String addProduct(Model model, @ModelAttribute("productForm") ProductRequest productForm,
                             @RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "5") int size,
                             @RequestParam(defaultValue = "id") String sortBy,
                             @RequestParam(defaultValue = "DESC") String sortType) throws JsonProcessingException {

        try {
            productAPI.addProduct(productForm);

            Page<ProductResponse> productResponses = productAPI.getProduct(page, size, sortBy, sortType);

            model.addAttribute("message", "Thêm sản phẩm thành công");
            model.addAttribute("currentPage", productResponses.getPageable().getPageNumber());
            model.addAttribute("totalPage", productResponses.getTotalPages());
            model.addAttribute("products", productResponses);

            return "Product";
        } catch (FeignException e) {
            Page<CategoryResponse> categoryResponses = productAPI.getCategory(page, size, sortBy, sortType);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(e.contentUTF8());

            model.addAttribute("message", jsonNode.get("message").asText());
            model.addAttribute("productForm", productForm);
            model.addAttribute("categories", categoryResponses);
            return "AddProduct";
        }
    }

    @PostMapping({"/product/update/{productId}"})
    public String updatesProduct(@ModelAttribute("productForm") ProductRequest productForm,
                                 Model model, ProductRequest productRequest,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "5") int size,
                                 @RequestParam(defaultValue = "id") String sortBy,
                                 @RequestParam(defaultValue = "DESC") String sortType,
                                 @PathVariable Long productId) throws JsonProcessingException {

        try {
            productForm.setId(productId);
            productAPI.updateProduct(productRequest, productId);
            Page<ProductResponse> productResponses = productAPI.getProduct(page, size, sortBy, sortType);
            Page<CategoryResponse> categoryResponses = productAPI.getCategory(page, size, sortBy, sortType);

            model.addAttribute("message", "Cập nhật thành công");
            model.addAttribute("categories", categoryResponses);
            model.addAttribute("currentPage", productResponses.getPageable().getPageNumber());
            model.addAttribute("totalPage", productResponses.getTotalPages());
            model.addAttribute("products", productResponses);
            return "Product";
        } catch (FeignException e) {
            Page<CategoryResponse> categoryResponses = productAPI.getCategory(page, size, sortBy, sortType);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(e.contentUTF8());

            model.addAttribute("message", jsonNode.get("message").asText());
            model.addAttribute("categories", categoryResponses);
            model.addAttribute("productForm", productForm);
            return "AddProduct";
        }
    }

    @GetMapping("category/delete/{categoryId}")
    public String deleteCategory(Model model, @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "5") int size,
                                @RequestParam(defaultValue = "id") String sortBy,
                                @RequestParam(defaultValue = "DESC") String sortType,
                                @PathVariable Long categoryId) throws JsonProcessingException {
        try {
            productAPI.deleteCategory(categoryId);

            Page<CategoryResponse> categoryResponses = productAPI.getCategory(page, size, sortBy, sortType);

            model.addAttribute("message", "Xóa sản phẩm thành công");
            model.addAttribute("categories", categoryResponses);
            return "Category";
        } catch (FeignException e) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(e.contentUTF8());
            Page<CategoryResponse> categoryResponses = productAPI.getCategory(page, size, sortBy, sortType);

            model.addAttribute("message", jsonNode.get("message").asText());
            model.addAttribute("categories", categoryResponses);
            return "Category";
        }
    }

    @GetMapping("product/delete/{productId}")
    public String deleteProduct(Model model, @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "5") int size,
                                @RequestParam(defaultValue = "id") String sortBy,
                                @RequestParam(defaultValue = "DESC") String sortType,
                                @PathVariable Long productId) {
        productAPI.deleteProduct(productId);

        Page<ProductResponse> productResponses = productAPI.getProduct(page, size, sortBy, sortType);

        model.addAttribute("message", "Xóa sản phẩm thành công");
        model.addAttribute("currentPage", productResponses.getPageable().getPageNumber());
        model.addAttribute("totalPage", productResponses.getTotalPages());
        model.addAttribute("products", productResponses);
        return "Product";
    }

    @GetMapping("bill/delete/{billId}")
    public String deleteBill(Model model, @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "5") int size,
                                @RequestParam(defaultValue = "id") String sortBy,
                                @RequestParam(defaultValue = "DESC") String sortType,
                                @PathVariable Long billId) {
        billAPI.deleteBill(billId);

        Page<BillResponse> billResponses = billAPI.getBill(page, size, sortBy, sortType);

        model.addAttribute("bills", billResponses);

        return "Bill";
    }

}
