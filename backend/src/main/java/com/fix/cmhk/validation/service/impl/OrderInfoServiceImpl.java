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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.retry.support.RetryTemplate;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.Objects;
import java.util.ArrayList;
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
    public String processMonthlyAIData(String monthDate) {
        log.info("开始处理月度AI质检数据，处理月份: {}", monthDate);
        try {
            // 解析日期范围
            LocalDateTime startDate = LocalDate.parse(monthDate + "-01").atStartOfDay();
            LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);
            log.info("解析月份范围 - 开始日期: {}, 结束日期: {}", 
                startDate.toLocalDate(), endDate.toLocalDate());

            // 查询指定月份内未处理的工单
            List<OrderInfo> orders = orderInfoRepository.findByCreateDateBetweenAndIsAIProcessed(
                startDate, endDate, 0);
            log.info("查询到待处理工单数量: {}", orders.size());
            
            // 记录查询条件用于调试
            log.debug("查询条件 - 开始日期: {}, 结束日期: {}, AI处理状态: {}", 
                startDate.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                endDate.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                0);

            if (orders.isEmpty()) {
                log.warn("未找到需要处理的工单数据");
                return "no data to process";
            }

            // 记录找到的工单的appointmentDate，用于调试
            orders.forEach(order -> 
                log.debug("工单 {} 的预约时间: {}", 
                    order.getJobNo(), 
                    order.getAppointmentDate()));

            int successCount = 0;
            int failCount = 0;
            int totalCount = orders.size();

            for (OrderInfo order : orders) {
                try {
                    log.info("开始处理工单: {}, 当前进度: {}/{}", order.getJobNo(), 
                        successCount + failCount + 1, totalCount);

                    // 验证带宽
                    boolean speedValid = validateSpeed(order);
                    log.debug("工单 {} 带宽验证结果: {}", order.getJobNo(), speedValid);

                    // 验证光功率
                    boolean powerValid = validateOpticalPower(order);
                    log.debug("工单 {} 光功率验证结果: {}", order.getJobNo(), powerValid);

                    // 更新工单状态
                    if (speedValid && powerValid) {
                        order.setAutoSuccess(1);
                        order.setQualityStatus("autoSuccess");
                        successCount++;
                        log.info("工单 {} 验证通过", order.getJobNo());
                    } else {
                        order.setAutoSuccess(0);
                        order.setQualityStatus("autoFail");
                        failCount++;
                        log.info("工单 {} 验证不通过 - 带宽验证: {}, 光功率验证: {}", 
                            order.getJobNo(), speedValid, powerValid);
                    }

                    order.setIsAIProcessed(1);
                    orderInfoRepository.save(order);
                    log.debug("工单 {} 状态已更新并保存", order.getJobNo());

                } catch (Exception e) {
                    log.error("处理工单 {} 时发生错误: {}", order.getJobNo(), e.getMessage(), e);
                    order.setAutoSuccess(-1);
                    order.setQualityStatus("error");
                    order.setIsAIProcessed(1);
                    orderInfoRepository.save(order);
                    failCount++;
                }
            }

            String result = String.format("处理完成 - 总数: %d, 成功: %d, 失败: %d", 
                totalCount, successCount, failCount);
            log.info(result);
            return "success";

        } catch (Exception e) {
            log.error("处理月度AI质检数据时发生错误: {}", e.getMessage(), e);
            return "error: " + e.getMessage();
        }
    }
    
    private boolean validateSpeed(OrderInfo order) {
        log.debug("验证工单 {} 的带宽数据", order.getJobNo());
        try {
            String uploadSpeedStr = Optional.ofNullable(order.getUploadSpeed())
                .orElse(order.getUploadSpeedManual());
            String downloadSpeedStr = Optional.ofNullable(order.getDownloadSpeed())
                .orElse(order.getDownloadSpeedManual());
            
            if (uploadSpeedStr == null || downloadSpeedStr == null) {
                log.warn("工单 {} 缺少带宽数据 - 上传: {}, 下载: {}", 
                    order.getJobNo(), uploadSpeedStr, downloadSpeedStr);
                return false;
            }

            double uploadSpeed = Double.parseDouble(uploadSpeedStr);
            double downloadSpeed = Double.parseDouble(downloadSpeedStr);
            boolean isValid = uploadSpeed >= 100 && downloadSpeed >= 100;
            
            log.debug("工单 {} 带宽验证结果: {} (上传: {}, 下载: {})", 
                order.getJobNo(), isValid, uploadSpeed, downloadSpeed);
            return isValid;
        } catch (Exception e) {
            log.error("验证工单 {} 带宽时发生错误: {}", order.getJobNo(), e.getMessage());
            return false;
        }
    }
    
    private boolean validateOpticalPower(OrderInfo order) {
        log.debug("验证工单 {} 的光功率数据", order.getJobNo());
        try {
            String fmPowerStr = Optional.ofNullable(order.getFmOutputPower())
                .orElse(order.getFmOutputPowerManual());
            String odbPowerStr = Optional.ofNullable(order.getOdbPowerMeter())
                .orElse(order.getOdbPowerMeterManual());
            
            if (fmPowerStr == null || odbPowerStr == null) {
                log.warn("工单 {} 缺少光功率数据 - FM功率: {}, ODB功率: {}", 
                    order.getJobNo(), fmPowerStr, odbPowerStr);
                return false;
            }

            double fmPower = Double.parseDouble(fmPowerStr);
            double odbPower = Double.parseDouble(odbPowerStr);
            boolean isValid = fmPower >= -8 && fmPower <= 2 && 
                             odbPower >= -8 && odbPower <= 2;
            
            log.debug("工单 {} 光功率验证结果: {} (FM功率: {}, ODB功率: {})", 
                order.getJobNo(), isValid, fmPower, odbPower);
            return isValid;
        } catch (Exception e) {
            log.error("验证工单 {} 光功率时发生错误: {}", order.getJobNo(), e.getMessage());
            return false;
        }
    }

    @Override
    public Page<OrderInfo> searchOrders(
        String jobNo,
        String customerOrderId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer autoSuccess,
        PageRequest pageRequest
    ) {
        Specification<OrderInfo> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (jobNo != null && !jobNo.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("jobNo"), "%" + jobNo + "%"));
            }
            
            if (customerOrderId != null && !customerOrderId.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("customerOrderId"), "%" + customerOrderId + "%"));
            }
            
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createDate"), startDate));
            }
            
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createDate"), endDate));
            }
            
            if (autoSuccess != null) {
                predicates.add(criteriaBuilder.equal(root.get("autoSuccess"), autoSuccess));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        return orderInfoRepository.findAll(specification, pageRequest);
    }
} 