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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.fix.cmhk.validation.model.dto.DuplicateCheckResponse;

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

    @Override
    public List<OrderInfo> findYesterdayOrders() {
        LocalDateTime startOfYesterday = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime endOfYesterday = LocalDate.now().atStartOfDay();
        return orderInfoRepository.findByCreateDateBetween(startOfYesterday, endOfYesterday);
    }
    
    @Override
    public List<OrderInfo> findLastMonthOrders() {
        LocalDate now = LocalDate.now();
        LocalDateTime startOfLastMonth = now.minusMonths(1).withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfLastMonth = now.withDayOfMonth(1).atStartOfDay().minusSeconds(1);
        return orderInfoRepository.findByCreateDateBetween(startOfLastMonth, endOfLastMonth);
    }
    
    @Override
    public List<OrderInfo> findLastYearOrders() {
        LocalDate now = LocalDate.now();
        LocalDateTime startOfLastYear = now.minusYears(1).withDayOfYear(1).atStartOfDay();
        LocalDateTime endOfLastYear = now.withDayOfYear(1).atStartOfDay().minusSeconds(1);
        return orderInfoRepository.findByCreateDateBetween(startOfLastYear, endOfLastYear);
    }
    
    @Override
    public Page<OrderInfo> findYesterdayOrders(Pageable pageable) {
        LocalDateTime startOfYesterday = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime endOfYesterday = LocalDate.now().atStartOfDay();
        return orderInfoRepository.findByCreateDateBetween(startOfYesterday, endOfYesterday, pageable);
    }
    
    @Override
    public Page<OrderInfo> findLastMonthOrders(Pageable pageable) {
        LocalDate now = LocalDate.now();
        LocalDateTime startOfLastMonth = now.minusMonths(1).withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfLastMonth = now.withDayOfMonth(1).atStartOfDay().minusSeconds(1);
        return orderInfoRepository.findByCreateDateBetween(startOfLastMonth, endOfLastMonth, pageable);
    }
    
    @Override
    public Page<OrderInfo> findLastYearOrders(Pageable pageable) {
        LocalDate now = LocalDate.now();
        LocalDateTime startOfLastYear = now.minusYears(1).withDayOfYear(1).atStartOfDay();
        LocalDateTime endOfLastYear = now.withDayOfYear(1).atStartOfDay().minusSeconds(1);
        return orderInfoRepository.findByCreateDateBetween(startOfLastYear, endOfLastYear, pageable);
    }

    @Override
    public Optional<OrderInfo> findBySpeedTestRefNo(Integer speedTestRefNo) {
        return orderInfoRepository.findBySpeedTestRefNo(speedTestRefNo);
    }
    
    @Override
    public Optional<OrderInfo> findBySnCode(String snCode) {
        return orderInfoRepository.findBySnCode(snCode);
    }
    
    @Override
    public Optional<OrderInfo> findByOcrContractId(String ocrContractId) {
        return orderInfoRepository.findByOcrContractId(ocrContractId);
    }
    
    @Override
    public List<OrderInfo> findBySpeedTestIP(String speedTestIP) {
        return orderInfoRepository.findBySpeedTestIP(speedTestIP);
    }

    @Override
    public DuplicateCheckResponse checkSpeedTestIPDuplicate(String ip) {
        long count = orderInfoRepository.countBySpeedTestIP(ip);
        boolean isDuplicate = count > 2;
        return new DuplicateCheckResponse(
            isDuplicate ? "fail" : "success",
            isDuplicate ? "IP地址重复使用超过2次" : "IP地址使用正常",
            (int) count
        );
    }
    
    @Override
    public DuplicateCheckResponse checkSpeedTestRefNoDuplicate(Integer refNo) {
        long count = orderInfoRepository.countBySpeedTestRefNo(refNo);
        boolean isDuplicate = count > 2;
        return new DuplicateCheckResponse(
            isDuplicate ? "fail" : "success",
            isDuplicate ? "速度测试参考号重复使用超过2次" : "速度测试参考号使用正常",
            (int) count
        );
    }
    
    @Override
    public DuplicateCheckResponse checkSnCodeDuplicate(String snCode) {
        long count = orderInfoRepository.countBySnCode(snCode);
        boolean isDuplicate = count > 2;
        return new DuplicateCheckResponse(
            isDuplicate ? "fail" : "success",
            isDuplicate ? "SN码重复使用超过2次" : "SN码使用正常",
            (int) count
        );
    }
    
    @Override
    public DuplicateCheckResponse checkOcrContractIdDuplicate(String ocrContractId) {
        long count = orderInfoRepository.countByOcrContractId(ocrContractId);
        boolean isDuplicate = count > 2;
        return new DuplicateCheckResponse(
            isDuplicate ? "fail" : "success",
            isDuplicate ? "OCR合同ID重复使用超过2次" : "OCR合同ID使用正常",
            (int) count
        );
    }
} 