package com.example.template.controller;

import com.example.template.FeignClient.BillAPI;
import com.example.template.FeignClient.ProductAPI;
import com.example.template.dto.request.BillRequest;
import com.example.template.dto.response.CategoryResponse;
import com.example.template.dto.response.ProductResponse;
import com.example.template.model.Bill;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TemplateUserController {
    private final ProductAPI productAPI;

    private final BillAPI billAPI;

    @GetMapping({"index/","index"})
    public String shopping(Model model, @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(defaultValue = "id") String sortBy,
                           @RequestParam(defaultValue = "DESC") String sortType) {

        Page<CategoryResponse> categoryResponses = productAPI.getCategory(page, size, sortBy, sortType);
        Page<ProductResponse> productResponses = productAPI.getProduct(page, size, sortBy, sortType);

        model.addAttribute("categories", categoryResponses);
        model.addAttribute("products", productResponses);
        return "User";
    }

    @GetMapping("/index/category/{categoryId}")
    public String findByCategory(@PathVariable("categoryId") Long categoryId, Model model,
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(defaultValue = "id") String sortBy,
                                @RequestParam(defaultValue = "DESC") String sortType) {
        Page<CategoryResponse> categoryResponses = productAPI.getCategory(page, size, sortBy, sortType);
        List<ProductResponse> productResponses = productAPI.getProductByCategoryId(categoryId);

        model.addAttribute("categories", categoryResponses);
        model.addAttribute("products", productResponses);

        return "User";
    }

    @GetMapping("/payment/{productId}")
    public String formPayment(Model model, @PathVariable("productId") Long productId) {
        ProductResponse productResponse = productAPI.getProductById(productId);
        Bill bill = new Bill();

        model.addAttribute("bill", bill);
        model.addAttribute("product", productResponse);

        return "Payment";
    }

    @GetMapping("/search")
    public String search(Model model, @RequestParam("keyword") String keyword,
                         @RequestParam(defaultValue = "1") int page,
                         @RequestParam(defaultValue = "10") int size,
                         @RequestParam(defaultValue = "id") String sortBy,
                         @RequestParam(defaultValue = "DESC") String sortType) {
        Page<CategoryResponse> categoryResponses = productAPI.getCategory(page, size, sortBy, sortType);
        List<ProductResponse> productResponses = productAPI.search(keyword);
        if(productResponses.size() == 0) {
            model.addAttribute("search", "Không có sản phẩm nào có '" + keyword + "' đâu!!");
        }

        model.addAttribute("categories", categoryResponses);
        model.addAttribute("products", productResponses);

        return "User";
    }

    @PostMapping("/payment/{productId}")
    public String payment(@ModelAttribute("billRequest") BillRequest billRequest,
                          Model model, @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "id") String sortBy,
                        @RequestParam(defaultValue = "DESC") String sortType,
                          @PathVariable Long productId) throws JsonProcessingException {
        try {
            billAPI.addBill(billRequest);

            Page<CategoryResponse> categoryResponses = productAPI.getCategory(page, size, sortBy, sortType);
            Page<ProductResponse> productResponses = productAPI.getProduct(page, size, sortBy, sortType);

            model.addAttribute("message", "Mua hàng thành công");
            model.addAttribute("categories", categoryResponses);
            model.addAttribute("products", productResponses);

            return "User";
        } catch (FeignException e) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(e.contentUTF8());
            model.addAttribute("message", jsonNode.get("message").asText());

            ProductResponse productResponse = productAPI.getProductById(productId);
            Bill bill = new Bill();

            model.addAttribute("bill", bill);
            model.addAttribute("product", productResponse);
            return "Payment";
        }
    }
}
