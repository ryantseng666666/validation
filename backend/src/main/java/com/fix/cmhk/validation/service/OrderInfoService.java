package com.fix.cmhk.validation.service;

import com.fix.cmhk.validation.model.entity.OrderInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrderInfoService {
    // 创建工单
    OrderInfo createOrder(OrderInfo orderInfo);
    
    // 更新工单
    OrderInfo updateOrder(String jobNo, OrderInfo orderInfo);
    
    // 删除工单
    void deleteOrder(String jobNo);
    
    // 根据工单号查询
    Optional<OrderInfo> findByJobNo(String jobNo);
    
    // 根据合同ID查询
    Optional<OrderInfo> findByContractId(String contractId);
    
    // 根据客户ID查询
    List<OrderInfo> findByCustomerId(String customerId);
    
    // 分页查询 - 根据客户ID
    Page<OrderInfo> findByCustomerId(String customerId, Pageable pageable);
    
    // 分页查询 - 根据合同ID
    Page<OrderInfo> findByContractId(String contractId, Pageable pageable);
    
    // 分页查询所有工单
    Page<OrderInfo> findAll(Pageable pageable);
} 