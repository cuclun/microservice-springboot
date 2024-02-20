package com.example.bill.controller;

import com.example.bill.dto.request.BillRequest;
import com.example.bill.dto.response.BillResponse;
import com.example.bill.service.BillServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/bill")
@RequiredArgsConstructor
public class BillController {

    private final BillServiceImpl billService;

    @GetMapping(path = {"/", ""})
    public ResponseEntity<?> bills(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "5") int size,
                                   @RequestParam(defaultValue = "id") String sortBy,
                                   @RequestParam(defaultValue = "DESC") String sortType, @RequestHeader HttpHeaders headers) {
        String accessToken = headers.getFirst("Authorization");
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.fromString(sortType), sortBy));
        Page<BillResponse> billResponses = billService.findAll(pageable, accessToken);

        return ResponseEntity.ok(billResponses);
    }

    @PostMapping(path = {"/", ""})
    public ResponseEntity<?> addBill(@RequestBody BillRequest billRequest, @RequestHeader HttpHeaders headers) {
        String accessToken = headers.getFirst("Authorization");
        try {
            billService.save(billRequest, accessToken);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errorCode", 400,
                    "message", e.getMessage()));
        }
    }

    @DeleteMapping(path = {"/{billId}", "/{billId}/"})
    public ResponseEntity<?> deleteBill(@PathVariable Long billId) {
        try {
            billService.delete(billId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errorCode", 400,
                    "message", e.getMessage()));
        }
    }

}
