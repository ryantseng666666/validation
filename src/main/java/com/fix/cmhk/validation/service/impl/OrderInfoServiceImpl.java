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
import java.util.Arrays;
import java.util.Objects;
import java.lang.reflect.Field;

import com.fix.cmhk.validation.model.dto.DuplicateCheckResponse;
import com.fix.cmhk.validation.model.entity.OrderInfoUpdateDetail;
import com.fix.cmhk.validation.repository.OrderInfoUpdateDetailRepository;
import com.fix.cmhk.validation.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import com.fix.cmhk.validation.controller.OpticalPowerController;
import com.fix.cmhk.validation.controller.SNCodeController;
import com.fix.cmhk.validation.controller.SpeedTestController;
import com.fix.cmhk.validation.model.entity.SpeedTestResult;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@Slf4j
@Service
@Transactional
public class OrderInfoServiceImpl implements OrderInfoService {

    @Autowired
    private OrderInfoRepository orderInfoRepository;

    @Autowired
    private OrderInfoUpdateDetailRepository updateDetailRepository;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private OpticalPowerController opticalPowerController;

    @Autowired
    private SNCodeController snCodeController;

    @Autowired
    private SpeedTestController speedTestController;

    @Autowired
    private RetryTemplate retryTemplate;

    private static final List<String> TRACKED_FIELDS = Arrays.asList(
        "adminUploadSpeed",
        "adminDownloadSpeed",
        "adminSocketOpticalPower",
        "adminFmOpticalPower",
        "adminContractId",
        "adminSn"
    );

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
    public OrderInfo updateOrder(String jobNo, OrderInfo newOrderInfo) {
        OrderInfo existingOrder = orderInfoRepository.findByJobNo(jobNo)
                .orElseThrow(() -> new EntityNotFoundException("工单不存在: " + jobNo));
        
        // Get all declared fields from OrderInfo class
        Field[] allFields = OrderInfo.class.getDeclaredFields();
        
        // Track changes for all fields
        for (Field field : allFields) {
            try {
                field.setAccessible(true);
                Object oldValue = field.get(existingOrder);
                Object newValue = field.get(newOrderInfo);
                
                // Skip if field is null in new order or values are equal
                if (newValue == null || Objects.equals(oldValue, newValue)) {
                    continue;
                }
                
                // Create update detail record
                OrderInfoUpdateDetail detail = OrderInfoUpdateDetail.builder()
                    .jobNo(jobNo)
                    .fieldName(field.getName())
                    .oldValue(oldValue != null ? oldValue.toString() : null)
                    .newValue(newValue.toString())
                    .updateTime(LocalDateTime.now())
                    .updateBy(securityUtils.getCurrentUsername())
                    .build();
                
                updateDetailRepository.save(detail);
                log.info("Field {} updated for job {}: {} -> {}", 
                    field.getName(), jobNo, oldValue, newValue);
                
            } catch (Exception e) {
                log.error("Error tracking field changes for {}: {}", field.getName(), e.getMessage(), e);
            }
        }
        
        // Update the order with new values
        newOrderInfo.setJobNo(jobNo);
        BeanUtils.copyProperties(newOrderInfo, existingOrder, "jobNo");
        
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

    @Override
    public List<OrderInfoUpdateDetail> getUpdateHistory(String jobNo) {
        return updateDetailRepository.findByJobNoOrderByUpdateTimeDesc(jobNo);
    }

    @Override
    @Transactional
    public String processMonthlyData(String monthDate) {
        try {
            List<OrderInfo> orders = orderInfoRepository.findByCreateDateAndIsAIProcessed(monthDate, 0);
            
            for (OrderInfo order : orders) {
                processOrder(order);
            }
            
            return "success";
        } catch (Exception e) {
            log.error("Failed to process monthly data for {}: {}", monthDate, e.getMessage(), e);
            return "fail";
        }
    }

    private void processOrder(OrderInfo order) {
        try {
            // 逻辑1: 数据预处理
            retryTemplate.execute(context -> {
                preprocessOrderData(order);
                return null;
            });

            // 逻辑2: 查重处理
            retryTemplate.execute(context -> {
                checkDuplicates(order);
                return null;
            });

            // 逻辑3-5: 验证和质检判定
            retryTemplate.execute(context -> {
                validateAndSetQualityStatus(order);
                return null;
            });

            // 更新处理标记
            order.setIsAIProcessed(1);
            orderInfoRepository.save(order);

        } catch (Exception e) {
            log.error("Failed to process order {}: {}", order.getId(), e.getMessage(), e);
            throw new RuntimeException("Order processing failed", e);
        }
    }

    private void preprocessOrderData(OrderInfo order) {
        // 调用各个预测接口
        order.setFmOutputPower(opticalPowerController.predict(order.getFmOutputPowerSnapshot()));
        order.setOdbPowerMeter(opticalPowerController.predict(order.getOdbPowerMeterSnapshot()));
        order.setSnCode(snCodeController.predict(order.getSnSnapshot()));
        order.setOcrContractId(snCodeController.predictContract(order.getContractIDSnapshot()));
        
        SpeedTestResult speedTest = speedTestController.predict(order.getSpeedTestResult());
        order.setUploadSpeed(speedTest.getUploadSpeed());
        order.setDownloadSpeed(speedTest.getDownloadSpeed());
        order.setSpeedTestRefNo(speedTest.getRefNo());
        order.setSpeedTestIP(speedTest.getIp());
    }

    private void validateAndSetQualityStatus(OrderInfo order) {
        // 验证上传速度
        validateUploadSpeed(order);
        
        // 验证下载速度
        validateDownloadSpeed(order);
        
        // 验证光功率
        validateOpticalPower(order);
        
        // 设置最终质检状态
        setFinalQualityStatus(order);
    }

    private void validateUploadSpeed(OrderInfo order) {
        double uploadSpeed = order.getUploadSpeed() != null && order.getUploadSpeed() > 0 
            ? order.getUploadSpeed() 
            : order.getUploadSpeedManual();
            
        order.setUploadSpeedSuccess(
            uploadSpeed > 0.8 * order.getBandwidth() ? 1 : 0
        );
    }

    private void validateDownloadSpeed(OrderInfo order) {
        double downloadSpeed = order.getDownloadSpeed() != null && order.getDownloadSpeed() > 0 
            ? order.getDownloadSpeed() 
            : order.getDownloadSpeedManual();
            
        order.setDownloadSpeedSuccess(
            downloadSpeed > 0.8 * order.getBandwidth() ? 1 : 0
        );
    }

    private void validateOpticalPower(OrderInfo order) {
        double fmPower = order.getFmOutputPower() != null ? 
            order.getFmOutputPower() : order.getFmOutputPowerManual();
        double odbPower = order.getOdbPowerMeter() != null ? 
            order.getOdbPowerMeter() : order.getOdbPowerMeterManual();

        order.setOpticalDiffSuccess(
            (fmPower - odbPower) <= 1.6 && odbPower <= -26 ? 1 : 0
        );
    }

    private void setFinalQualityStatus(OrderInfo order) {
        boolean isAutoSuccess = 
            order.getSpeedTestIpDuplicate() == 1 &&
            order.getSpeedTestRefIsDuplicate() == 1 &&
            order.getSnIsDuplicate() == 1 &&
            order.getContractIdIsDuplicate() == 1 &&
            order.getUploadSpeedSuccess() == 1 &&
            order.getDownloadSpeedSuccess() == 1 &&
            order.getOpticalDiffSuccess() == 1 &&
            (order.getItemStatus() == null || "Y".equals(order.getItemStatus()));

        order.setQualityStatus(isAutoSuccess ? "autoSuccess" : "autoFail");
    }

    @Override
    @Transactional
    public String checkDuplicates(String monthDate) {
        try {
            List<OrderInfo> orders = orderInfoRepository.findByCreateDate(monthDate);
            
            for (OrderInfo order : orders) {
                checkDuplicates(order);
                orderInfoRepository.save(order);
            }
            
            return "success";
        } catch (Exception e) {
            log.error("Failed to check duplicates for {}: {}", monthDate, e.getMessage(), e);
            return "fail";
        }
    }

    private void checkDuplicates(OrderInfo order) {
        // 检查各项重复
        order.setSpeedTestIpDuplicate(checkSpeedTestIPDuplicate(order.getSpeedTestIP()));
        order.setSpeedTestRefIsDuplicate(checkSpeedTestRefNoDuplicate(order.getSpeedTestRefNo()));
        order.setSnIsDuplicate(checkSnCodeDuplicate(order.getSnCode()));
        order.setContractIdIsDuplicate(checkOcrContractIdDuplicate(order.getOcrContractId()));

        // 设置合约和SN成功标志
        order.setContractAndSNSuccess(
            order.getSnIsDuplicate() == 1 && order.getContractIdIsDuplicate() == 1 ? 1 : 0
        );
    }
} 