package com.example.bill.repository;

import com.example.bill.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    boolean existsByProductId(Long id);
}
