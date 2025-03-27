package com.fix.cmhk.validation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderInfoRepository extends JpaRepository<OrderInfo, String> {
    
    @Query("SELECT o FROM OrderInfo o WHERE YEAR(o.createDate) = :year AND MONTH(o.createDate) = :month AND o.isAIProcessed = 0")
    List<OrderInfo> findUnprocessedOrdersByMonth(Integer year, Integer month);
    
    @Query("SELECT o FROM OrderInfo o WHERE YEAR(o.createDate) = :year AND MONTH(o.createDate) = :month")
    List<OrderInfo> findOrdersByMonth(Integer year, Integer month);
    
    @Query("SELECT COUNT(o) FROM OrderInfo o WHERE o.speedTestIP = :ip")
    int countDuplicateIP(String ip);
    
    @Query("SELECT COUNT(o) FROM OrderInfo o WHERE o.speedTestRefNo = :refNo")
    int countDuplicateRefNo(String refNo);
    
    @Query("SELECT COUNT(o) FROM OrderInfo o WHERE o.snCode = :snCode")
    int countDuplicateSN(String snCode);
    
    @Query("SELECT COUNT(o) FROM OrderInfo o WHERE o.ocrContractId = :contractId")
    int countDuplicateContractId(String contractId);
} 