package com.example.bill.service;

import com.example.bill.dto.request.BillRequest;
import com.example.bill.dto.response.BillResponse;
import com.example.bill.dto.response.ProductResponse;
import com.example.bill.feign.APIProduct;
import com.example.bill.mapper.BillMapper;
import com.example.bill.model.Bill;
import com.example.bill.model.Product;
import com.example.bill.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class BillServiceImpl {

    private final APIProduct apiProduct;

    private final BillMapper billMapper;

    private final BillRepository billRepository;

    public Page<BillResponse> findAll(Pageable pageable, String accessToken) {
        Page<Bill> entities = billRepository.findAll(pageable);
        return entities.map(bill -> billConvertToBillResponse(bill, accessToken));
    }

    private BillResponse billConvertToBillResponse(Bill bill, String accessToken) {
        if (bill.getProductId() != null) {
            ProductResponse productResponse = apiProduct.getProductById(bill.getProductId(), accessToken);
            return billMapper.billToBillResponse(bill, productResponse);
        }
        return null;
    }

    public BillRequest save(BillRequest billRequest, String accessToken) {
        if (apiProduct.getProductById(billRequest.getProductId(), accessToken) == null) {
            throw new RuntimeException("Không tìm thấy sản phẩm");
        }
        if (billRequest.getQuantity() < 0) {
            throw new RuntimeException("Mua nhiều hơn đi");
        }
        int stock = apiProduct.getQuantityProduct(billRequest.getProductId(), accessToken);
        if (billRequest.getQuantity() > stock) {
            throw new RuntimeException("Còn " + apiProduct.getQuantityProduct(billRequest.getProductId(),
                    accessToken) + " sản " +
                    "phẩm thui");
        }
        apiProduct.changeQuantity(billRequest.getProductId(), billRequest.getQuantity(), accessToken);
        billRepository.save(billMapper.billRequestDTOToBill(billRequest));

        return billRequest;
    }

    public void delete(Long billId) {
        Optional<Bill> existsBill = billRepository.findById(billId);
        if (existsBill.isPresent()) {
            billRepository.deleteById(billId);
        } else {
            throw new RuntimeException("Hóa đơn không tồn tại");
        }
    }
}


