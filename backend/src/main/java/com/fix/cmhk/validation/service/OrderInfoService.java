package com.fix.cmhk.validation.service;

import com.fix.cmhk.validation.model.entity.OrderInfo;
import com.fix.cmhk.validation.model.dto.DuplicateCheckResponse;
import com.fix.cmhk.validation.model.entity.OrderInfoUpdateDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

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
    
    // 查询昨天的工单
    List<OrderInfo> findYesterdayOrders();
    
    // 查询上月的工单
    List<OrderInfo> findLastMonthOrders();
    
    // 查询去年的工单
    List<OrderInfo> findLastYearOrders();
    
    // 分页查询
    Page<OrderInfo> findYesterdayOrders(Pageable pageable);
    Page<OrderInfo> findLastMonthOrders(Pageable pageable);
    Page<OrderInfo> findLastYearOrders(Pageable pageable);
    
    Optional<OrderInfo> findBySpeedTestRefNo(String speedTestRefNo);
    Optional<OrderInfo> findBySnCode(String snCode);
    Optional<OrderInfo> findByOcrContractId(String ocrContractId);
    List<OrderInfo> findBySpeedTestIP(String speedTestIP);
    
    DuplicateCheckResponse checkSpeedTestIPDuplicate(String ip);
    DuplicateCheckResponse checkSpeedTestRefNoDuplicate(String refNo);
    DuplicateCheckResponse checkSnCodeDuplicate(String snCode);
    DuplicateCheckResponse checkOcrContractIdDuplicate(String ocrContractId);
    
    // Add new method to get update history
    List<OrderInfoUpdateDetail> getUpdateHistory(String jobNo);

    /**
     * 处理当月AI质检数据
     * @param monthDate 月份日期 (格式: yyyy-MM)
     * @return 处理结果
     */
    String processMonthlyAIData(String monthDate);

    /**
     * 条件查询工单
     * @param jobNo 工单号
     * @param customerOrderId 客户号
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param autoSuccess AI处理状态
     * @param pageRequest 分页参数
     * @return 分页工单列表
     */
    Page<OrderInfo> searchOrders(
        String jobNo,
        String customerOrderId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer autoSuccess,
        PageRequest pageRequest
    );
} 