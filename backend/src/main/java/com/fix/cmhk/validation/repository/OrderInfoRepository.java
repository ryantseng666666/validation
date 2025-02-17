package com.fix.cmhk.validation.repository;

import com.fix.cmhk.validation.model.entity.OrderInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
    
    // 根据创建日期查询（只比较日期部分）
    @Query("SELECT o FROM OrderInfo o WHERE DATE(o.createDate) BETWEEN DATE(:startDate) AND DATE(:endDate)")
    List<OrderInfo> findByCreateDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // 分页查询
    @Query("SELECT o FROM OrderInfo o WHERE DATE(o.createDate) BETWEEN DATE(:startDate) AND DATE(:endDate)")
    Page<OrderInfo> findByCreateDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    Optional<OrderInfo> findBySpeedTestRefNo(Integer speedTestRefNo);
    Optional<OrderInfo> findBySnCode(String snCode);
    Optional<OrderInfo> findByOcrContractId(String ocrContractId);
    List<OrderInfo> findBySpeedTestIP(String speedTestIP);
    
    @Query("SELECT COUNT(o) FROM OrderInfo o WHERE o.speedTestIP = :ip")
    long countBySpeedTestIP(String ip);
    
    @Query("SELECT COUNT(o) FROM OrderInfo o WHERE o.speedTestRefNo = :refNo")
    long countBySpeedTestRefNo(Integer refNo);
    
    @Query("SELECT COUNT(o) FROM OrderInfo o WHERE o.snCode = :snCode")
    long countBySnCode(String snCode);
    
    @Query("SELECT COUNT(o) FROM OrderInfo o WHERE o.ocrContractId = :ocrContractId")
    long countByOcrContractId(String ocrContractId);
} 