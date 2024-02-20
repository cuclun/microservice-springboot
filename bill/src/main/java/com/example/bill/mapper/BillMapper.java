package com.example.bill.mapper;

import com.example.bill.dto.request.BillRequest;
import com.example.bill.dto.response.BillResponse;
import com.example.bill.dto.response.ProductResponse;
import com.example.bill.feign.APIProduct;
import com.example.bill.model.Bill;
import com.example.bill.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BillMapper {

    @Mapping(target = "productResponse", source = "productResponse")
    @Mapping(target = "id", source = "bill.id")
    @Mapping(target = "quantity", source = "bill.quantity")
    BillResponse billToBillResponse(Bill bill, ProductResponse productResponse);

    Bill billRequestDTOToBill(BillRequest billRequest);
}