package com.fix.cmhk.validation.model.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoRequest {
    private String jobNo;
    private String contractId;
    private String title;
    private String orderType;
    private String customerOrderId;
    private String customerSubOrderId;
    private String ratePlan;
    private String serviceType;
    private String orderStatus;
    private String currentLink;
    private String workerId;
    private String workerName;
    private String workerPartner;
    private String resultCode;
    private String resultCodeDesc;
    private String customerId;
    private String customer;
    
    // 新增字段
    private Integer speedTestRefNo;
    private String speedTestIP;
    private String snCode;
    private String ocrContractId;
    
    private String adminUploadSpeed;
    private String adminDownloadSpeed;
    private String adminSocketOpticalPower;
    private String adminFmOpticalPower;
    private String adminContractId;
    private String adminSn;
    
    private String snCodeManual;
    
    private Integer uploadSpeedSuccess;
    private Integer downloadSpeedSuccess;
    private Integer contractAndSNSuccess;
    
    private Integer opticalDiffSuccess;
} 