package com.fix.cmhk.validation.service.impl;

import com.fix.cmhk.validation.model.OpticalPowerResponse;
import com.fix.cmhk.validation.model.PredictionRequest;
import com.fix.cmhk.validation.model.SNCodeResponse;
import com.fix.cmhk.validation.model.SpeedTestResponse;
import com.fix.cmhk.validation.model.entity.OrderInfo;
import com.fix.cmhk.validation.repository.OrderInfoRepository;
import com.fix.cmhk.validation.service.OrderInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.retry.support.RetryTemplate;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.Objects;
import java.lang.reflect.Field;
import java.util.Base64;

import com.fix.cmhk.validation.model.dto.DuplicateCheckResponse;
import com.fix.cmhk.validation.model.entity.OrderInfoUpdateDetail;
import com.fix.cmhk.validation.repository.OrderInfoUpdateDetailRepository;
import com.fix.cmhk.validation.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import com.fix.cmhk.validation.controller.OpticalPowerController;
import com.fix.cmhk.validation.controller.SNCodeController;
import com.fix.cmhk.validation.controller.SpeedTestController;
import com.fix.cmhk.validation.model.request.ImageRequest;
import com.fix.cmhk.validation.model.PredictionRequest;
import com.fix.cmhk.validation.model.OpticalPowerResponse;
import com.fix.cmhk.validation.model.SpeedTestResponse;

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
        
        // Track changes before update
        for (String fieldName : TRACKED_FIELDS) {
            try {
                Field field = OrderInfo.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                String oldValue = (String) field.get(existingOrder);
                String newValue = (String) field.get(newOrderInfo);
                
                if (!Objects.equals(oldValue, newValue)) {
                    // Create update detail record
                    OrderInfoUpdateDetail detail = OrderInfoUpdateDetail.builder()
                        .jobNo(jobNo)
                        .fieldName(fieldName)
                        .oldValue(oldValue)
                        .newValue(newValue)
                        .updateTime(LocalDateTime.now())
                        .updateBy(securityUtils.getCurrentUsername())
                        .build();
                    log.debug("test detail");
                    updateDetailRepository.save(detail);
                    log.info("Field {} updated for job {}: {} -> {}", 
                        fieldName, jobNo, oldValue, newValue);
                }
            } catch (Exception e) {
                log.error("Error tracking field changes for {}: {}", fieldName, e.getMessage(), e);
            }
        }
        
        // Update the order
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
    public Optional<OrderInfo> findBySpeedTestRefNo(String speedTestRefNo) {
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
    public DuplicateCheckResponse checkSpeedTestRefNoDuplicate(String refNo) {
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
    public String processMonthlyAIData(String monthDate) {
        try {
            return retryTemplate.execute(context -> {
                // 1. 获取需要处理的工单
                List<OrderInfo> orders = getUnprocessedOrders(monthDate);
                
                for (OrderInfo order : orders) {
                    try {
                        // 2. 数据预处理
                        preprocessOrder(order);
                        
                        // 3. 带宽验证
                        validateBandwidth(order);
                        
                        // 4. 光功率验证
                        validateOpticalPower(order);
                        
                        // 5. 最终质检判定
                        determineQualityStatus(order);
                        
                        // 6. 更新工单
                        orderInfoRepository.save(order);
                        
                    } catch (Exception e) {
                        log.error("处理工单失败 [jobNo={}]: {}", order.getJobNo(), e.getMessage(), e);
                        setErrorValues(order);
                        orderInfoRepository.save(order);
                    }
                }
                
                return "success";
            });
        } catch (Exception e) {
            log.error("处理当月AI质检数据失败: {}", e.getMessage(), e);
            return "fail";
        }
    }
    
    private List<OrderInfo> getUnprocessedOrders(String monthDate) {
        LocalDateTime startOfMonth = LocalDate.parse(monthDate + "-01").atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
        return orderInfoRepository.findByCreateDateBetweenAndIsAIProcessed(
            startOfMonth, endOfMonth, 0);
    }
    
    private void preprocessOrder(OrderInfo order) {
        // 处理 FM 输出功率
        if (order.getFmOutputPowerSnapshot() != null) {
            ImageRequest request = new ImageRequest();
            request.setBase64Image(Base64.getEncoder().encodeToString(order.getFmOutputPowerSnapshot().getBytes()));
            ResponseEntity<OpticalPowerResponse> response = opticalPowerController.predict(request);
            order.setFmOutputPower(response.getBody().getOpticalPower().toString());
        }
        
        // 处理 ODB 功率计
        if (order.getOdbPowerMeterSnapshot() != null) {
            ImageRequest request = new ImageRequest();
            request.setBase64Image(Base64.getEncoder().encodeToString(order.getOdbPowerMeterSnapshot().getBytes()));
            ResponseEntity<OpticalPowerResponse> response = opticalPowerController.predict(request);
            order.setOdbPowerMeter(response.getBody().getOpticalPower().toString());
        }
        
        // 处理 SN 码
        if (order.getSnCodeSnapshot() != null) {
            ImageRequest request = new ImageRequest();
            request.setBase64Image(Base64.getEncoder().encodeToString(order.getSnCodeSnapshot().getBytes()));
            ResponseEntity<SNCodeResponse> response = snCodeController.predict(request);
            order.setSnCode(response.getBody().getSnCode());
        }
        
        // 处理合同 ID
        if (order.getContractIdSnapshot() != null) {
            ImageRequest request = new ImageRequest();
            request.setBase64Image(Base64.getEncoder().encodeToString(order.getContractIdSnapshot().getBytes()));
            ResponseEntity<SNCodeResponse> response = snCodeController.predict(request);
            order.setOcrContractId(response.getBody().getSnCode());
        }
        
        // 处理速度测试
        if (order.getSpeedTestResult() != null) {
            PredictionRequest request = new PredictionRequest();
            request.setBase64Image(Base64.getEncoder().encodeToString(order.getSpeedTestResult().getBytes()));
            ResponseEntity<SpeedTestResponse> response = speedTestController.predict(request);
            order.setUploadSpeed(response.getBody().getUploadSpeed().toString());
            order.setDownloadSpeed(response.getBody().getDownloadSpeed().toString());
            order.setSpeedTestRefNo(response.getBody().getReferenceId()) ;
            order.setSpeedTestIP(response.getBody().getIpAddress());
        }
        
        // 设置重复检查标志
        order.setSpeedTestIpDuplicate(checkSpeedTestIPDuplicate(order.getSpeedTestIP()).getCount() <= 1 ? 0 : 1);
        order.setSpeedTestRefIsDuplicate(checkSpeedTestRefNoDuplicate(order.getSpeedTestRefNo()).getCount() <= 1 ? 0 : 1);
        order.setSnIsDuplicate(checkSnCodeDuplicate(order.getSnCode()).getCount() <= 1 ? 0 : 1);
        order.setContractIdIsDuplicate(checkOcrContractIdDuplicate(order.getOcrContractId()).getCount() <= 1 ? 0 : 1);
        order.setIsAIProcessed(1);
    }
    
    private void validateBandwidth(OrderInfo order) {
        // 验证上传速度
        String uploadSpeed = Optional.ofNullable(order.getUploadSpeed())
            .orElse(order.getUploadSpeedManual());
        if (uploadSpeed != null) {
            double speed = Double.parseDouble(uploadSpeed);
            double bandwidth = Double.parseDouble(order.getBandwidth());
            order.setUploadSpeedSuccess(speed > 0.8 * bandwidth ? 1 : 0);
        } else {
            order.setUploadSpeedSuccess(-1);
        }
        
        // 验证下载速度
        String downloadSpeed = Optional.ofNullable(order.getDownloadSpeed())
            .orElse(order.getDownloadSpeedManual());
        if (downloadSpeed != null) {
            double speed = Double.parseDouble(downloadSpeed);
            double bandwidth = Double.parseDouble(order.getBandwidth());
            order.setDownloadSpeedSuccess(speed > 0.8 * bandwidth ? 1 : 0);
        } else {
            order.setDownloadSpeedSuccess(-1);
        }
    }
    
    private void validateOpticalPower(OrderInfo order) {
        String fmPower = Optional.ofNullable(order.getFmOutputPower())
            .orElse(order.getFmOutputPowerManual());
        String odbPower = Optional.ofNullable(order.getOdbPowerMeter())
            .orElse(order.getOdbPowerMeterManual());
        
        if (fmPower != null && odbPower != null) {
            double fmValue = Double.parseDouble(fmPower);
            double odbValue = Double.parseDouble(odbPower);
            order.setOpticalDiffSuccess(
                (fmValue - odbValue <= 1.6 && odbValue <= -26) ? 1 : 0
            );
        } else {
            order.setOpticalDiffSuccess(-1);
        }
    }
    
    private void determineQualityStatus(OrderInfo order) {
        boolean isAutoSuccess = 
            order.getSpeedTestIpDuplicate() == 0 &&
            order.getSpeedTestRefIsDuplicate() == 0 &&
            order.getSnIsDuplicate() == 0 &&
            order.getContractIdIsDuplicate() == 0 &&
            order.getUploadSpeedSuccess() == 1 &&
            order.getDownloadSpeedSuccess() == 1 &&
            order.getOpticalDiffSuccess() == 1 &&
            (order.getItemStatus() == null || "Y".equals(order.getItemStatus()));
        
        order.setQualityStatus(isAutoSuccess ? "autoSuccess" : "autoFail");
        order.setAutoSuccess(isAutoSuccess ? 1 : 0);
    }
    
    private void setErrorValues(OrderInfo order) {
        order.setUploadSpeedSuccess(-1);
        order.setDownloadSpeedSuccess(-1);
        order.setOpticalDiffSuccess(-1);
        order.setAutoSuccess(-1);
        order.setQualityStatus("autoFail");
    }
} 