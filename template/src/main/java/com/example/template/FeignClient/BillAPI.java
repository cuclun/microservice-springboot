package com.example.template.FeignClient;

import com.example.template.dto.request.BillRequest;
import com.example.template.dto.response.BillResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "bill", url = "http://localhost:8080/api/bill")
public interface BillAPI {

    @GetMapping(path = "/")
    Page<BillResponse> getBill(@RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "5") int size,
                               @RequestParam(defaultValue = "id") String sortBy,
                               @RequestParam(defaultValue = "DESC") String sortType);

    @PostMapping(path = "/")
    BillRequest addBill(BillRequest billRequest);

    @DeleteMapping(path = "/{billId}")
    void deleteBill(@PathVariable Long billId);
}