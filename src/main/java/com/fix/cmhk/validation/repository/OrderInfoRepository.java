package com.fix.cmhk.validation.repository;

import com.fix.cmhk.validation.model.entity.OrderInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderInfoRepository extends JpaRepository<OrderInfo, Long>, JpaSpecificationExecutor<OrderInfo> {
    boolean existsByJobNo(String jobNo);
    Optional<OrderInfo> findByJobNo(String jobNo);
    Optional<OrderInfo> findByContractId(String contractId);
    List<OrderInfo> findByCustomerId(String customerId);
    Page<OrderInfo> findByCustomerId(String customerId, Pageable pageable);
    Page<OrderInfo> findByContractId(String contractId, Pageable pageable);
    List<OrderInfo> findByCreateDateBetween(LocalDateTime start, LocalDateTime end);
    Page<OrderInfo> findByCreateDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    Optional<OrderInfo> findBySpeedTestRefNo(String speedTestRefNo);
    Optional<OrderInfo> findBySnCode(String snCode);
    Optional<OrderInfo> findByOcrContractId(String ocrContractId);
    List<OrderInfo> findBySpeedTestIP(String speedTestIP);
    long countBySpeedTestIP(String speedTestIP);
    long countBySpeedTestRefNo(String speedTestRefNo);
    long countBySnCode(String snCode);
    long countByOcrContractId(String ocrContractId);
    
    @Query("SELECT o FROM OrderInfo o WHERE YEAR(o.createDate) = YEAR(:monthDate) AND MONTH(o.createDate) = MONTH(:monthDate) AND o.isAIProcessed = :processed")
    List<OrderInfo> findByCreateDateAndIsAIProcessed(@Param("monthDate") String monthDate, @Param("processed") Integer processed);
    
    @Query("SELECT o FROM OrderInfo o WHERE YEAR(o.createDate) = YEAR(:monthDate) AND MONTH(o.createDate) = MONTH(:monthDate)")
    List<OrderInfo> findByCreateDate(@Param("monthDate") String monthDate);
} 