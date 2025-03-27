package com.fix.cmhk.validation.model.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_info")
public class OrderInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String jobNo;
    private String contractId;
    private String customerOrderId;
    private String itemStatus;
    private LocalDateTime createDate;
    
    private Double uploadSpeed;
    private Double downloadSpeed;
    private Double uploadSpeedManual;
    private Double downloadSpeedManual;
    private Double bandwidth;
    
    private String speedTestRefNo;
    private String speedTestIP;
    
    private Double fmOutputPower;
    private Double odbPowerMeter;
    private Double fmOutputPowerManual;
    private Double odbPowerMeterManual;
    
    private String snCode;
    private String ocrContractId;
    
    @Lob
    private String fmOutputPowerSnapshot;
    @Lob
    private String odbPowerMeterSnapshot;
    @Lob
    private String snSnapshot;
    @Lob
    private String contractIDSnapshot;
    @Lob
    private String speedTestResult;
    
    private Integer isAIProcessed;
    private Integer speedTestIpDuplicate;
    private Integer speedTestRefIsDuplicate;
    private Integer snIsDuplicate;
    private Integer contractIdIsDuplicate;
    private Integer contractAndSNSuccess;
    private Integer uploadSpeedSuccess;
    private Integer downloadSpeedSuccess;
    private Integer opticalDiffSuccess;
    private String qualityStatus;
} 