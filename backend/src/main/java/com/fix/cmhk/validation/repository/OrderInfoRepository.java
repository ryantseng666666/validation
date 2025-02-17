package com.fix.cmhk.validation.repository;

import com.fix.cmhk.validation.model.entity.OrderInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderInfoRepository extends JpaRepository<OrderInfo, String>, JpaSpecificationExecutor<OrderInfo> {
    Optional<OrderInfo> findByJobNo(String jobNo);
    
    Optional<OrderInfo> findByContractId(String contractId);
    
    List<OrderInfo> findByCustomerId(String customerId);
    
    Page<OrderInfo> findByCustomerId(String customerId, Pageable pageable);
    
    Page<OrderInfo> findByContractId(String contractId, Pageable pageable);
    
    boolean existsByJobNo(String jobNo);
} 