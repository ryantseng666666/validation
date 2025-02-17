package com.fix.cmhk.validation.service.impl;

import com.fix.cmhk.validation.model.entity.OrderInfo;
import com.fix.cmhk.validation.repository.OrderInfoRepository;
import com.fix.cmhk.validation.service.OrderInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class OrderInfoServiceImpl implements OrderInfoService {

    @Autowired
    private OrderInfoRepository orderInfoRepository;

    @Override
    @Transactional
    public OrderInfo createOrder(OrderInfo orderInfo) {
        if (orderInfoRepository.existsByJobNo(orderInfo.getJobNo())) {
            throw new IllegalArgumentException("工单号已存在: " + orderInfo.getJobNo());
        }
        return orderInfoRepository.save(orderInfo);
    }

    @Override
    @Transactional
    public OrderInfo updateOrder(String jobNo, OrderInfo orderInfo) {
        OrderInfo existingOrder = orderInfoRepository.findByJobNo(jobNo)
                .orElseThrow(() -> new EntityNotFoundException("工单不存在: " + jobNo));
        
        // 保留ID，更新其他字段
        orderInfo.setJobNo(jobNo);
        BeanUtils.copyProperties(orderInfo, existingOrder, "jobNo");
        
        return orderInfoRepository.save(existingOrder);
    }

    @Override
    @Transactional
    public void deleteOrder(String jobNo) {
        if (!orderInfoRepository.existsByJobNo(jobNo)) {
            throw new EntityNotFoundException("工单不存在: " + jobNo);
        }
        orderInfoRepository.deleteById(jobNo);
    }

    @Override
    public Optional<OrderInfo> findByJobNo(String jobNo) {
        return orderInfoRepository.findByJobNo(jobNo);
    }

    @Override
    public Optional<OrderInfo> findByContractId(String contractId) {
        return orderInfoRepository.findByContractId(contractId);
    }

    @Override
    public List<OrderInfo> findByCustomerId(String customerId) {
        return orderInfoRepository.findByCustomerId(customerId);
    }

    @Override
    public Page<OrderInfo> findByCustomerId(String customerId, Pageable pageable) {
        return orderInfoRepository.findByCustomerId(customerId, pageable);
    }

    @Override
    public Page<OrderInfo> findByContractId(String contractId, Pageable pageable) {
        return orderInfoRepository.findByContractId(contractId, pageable);
    }

    @Override
    public Page<OrderInfo> findAll(Pageable pageable) {
        return orderInfoRepository.findAll(pageable);
    }
} 