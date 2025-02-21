package com.fix.cmhk.validation.model.entity;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "order_info")
public class OrderInfo {
    @Id
    private String jobNo;
    private String contractId;
    
    @Column(columnDefinition = "TEXT")
    private String title;
    
    private String orderType;
    private LocalDateTime createDate;
    private String customerOrderId;
    private String customerSubOrderId;
    private String ratePlan;
    private String serviceType;
    private String orderStatus;
    private String currentLink;
    private LocalDateTime appointmentDate;
    private LocalDateTime arriveTime;
    private LocalDateTime archiveTime;
    private String workerId;
    private String workerName;
    private String workerPartner;
    private String resultCode;
    
    @Column(columnDefinition = "TEXT")
    private String resultCodeDesc;
    
    private String customerId;
    private String customer;
    private String customerContact;
    
    @Column(columnDefinition = "TEXT")
    private String customerAddress;
    
    private String addressCode;
    private String area;
    private String region;
    private String district;
    private String cmhkBuildingId;
    
    @Column(columnDefinition = "TEXT")
    private String buildName;
    
    private String businessType;
    private String taskCategory;
    private String taskSubCategory;
    private String bandwidth;
    private String splitterId;
    private String splittingRatio;
    private String fmId;
    private String fmPortNo;
    private String fmName;
    private String fmCarrier;
    private String facilityType;
    private String preWiring;
    private String ponPort;
    private String oltName;
    private String oltVendor;
    private String oltType;
    private String oltIp;
    private String oltCarrier;
    private String ontVendor;
    private String ontOldSn;
    private String ontNewSn;
    private String routerType;
    private String routerSn;
    
    @Column(columnDefinition = "TEXT")
    private String oltTxOptPower;
    
    @Column(columnDefinition = "TEXT")
    private String ontRxOptPower;
    
    @Column(columnDefinition = "TEXT")
    private String ontTxOptPower;
    
    @Column(columnDefinition = "TEXT")
    private String oltRxOptPower;
    
    @Column(columnDefinition = "TEXT")
    private String speedTestResult;
    
    @Column(columnDefinition = "TEXT")
    private String downloadSpeed;
    
    @Column(columnDefinition = "TEXT")
    private String downloadSpeedManual;
    
    @Column(columnDefinition = "TEXT")
    private String uploadSpeed;
    
    @Column(columnDefinition = "TEXT")
    private String uploadSpeedManual;
    
    @Column(columnDefinition = "TEXT")
    private String fmOutputPowerSnapshot;
    
    @Column(columnDefinition = "TEXT")
    private String fmOutputPower;
    
    @Column(columnDefinition = "TEXT")
    private String fmOutputPowerManual;
    
    @Column(columnDefinition = "TEXT")
    private String odbPowerMeterSnapshot;
    
    @Column(columnDefinition = "TEXT")
    private String odbPowerMeter;
    
    @Column(columnDefinition = "TEXT")
    private String odbPowerMeterManual;
    
    @Column(columnDefinition = "TEXT")
    private String ontLedStatus;
    
    @Column(columnDefinition = "TEXT")
    private String ontLedLightManual;
    
    @Column(columnDefinition = "TEXT")
    private String lineLabelManual;
    
    @Column(columnDefinition = "TEXT")
    private String ontLabelManual;
    
    @Column(columnDefinition = "TEXT")
    private String signedUat;
    
    @Column(columnDefinition = "TEXT")
    private String beforeActivityPhoto;
    
    @Column(columnDefinition = "TEXT")
    private String floorDifference;
    
    private String currentHandler;
    private String itemStatus;
    private String qualityStatus;
    
    @Column(columnDefinition = "TEXT")
    private String qualityRemark;
    
    @Column(columnDefinition = "TEXT")
    private String opticalPowerAutoResult;
    
    @Column(columnDefinition = "TEXT")
    private String opticalPowerManualResult;
    
    @Column(columnDefinition = "TEXT")
    private String speedAutoResult;
    
    @Column(columnDefinition = "TEXT")
    private String speedManualResult;
    
    private String isCharge;
    private String wifiPlan;
    private String sVlan;
    private String cVlan;
    
    private Integer speedTestRefNo;
    
    @Column(name = "speed_test_IP")
    private String speedTestIP;
    
    @Column(name = "SN_code")
    private String snCode;
    
    @Column(name = "ocr_contract_id")
    private String ocrContractId;

    @Column(name = "admin_upload_speed")
    private String adminUploadSpeed;
    
    @Column(name = "admin_download_speed")
    private String adminDownloadSpeed;
    
    @Column(name = "admin_socket_optical_power")
    private String adminSocketOpticalPower;
    
    @Column(name = "admin_fm_optical_power")
    private String adminFmOpticalPower;
    
    @Column(name = "admin_contract_id")
    private String adminContractId;
    
    @Column(name = "admin_sn")
    private String adminSn;

    @Column(name = "sn_code_manual")
    private String snCodeManual;

    @Column(name = "upload_speed_success")
    private Integer uploadSpeedSuccess;
    
    @Column(name = "download_speed_success")
    private Integer downloadSpeedSuccess;
    
    @Column(name = "contract_and_sn_success")
    private Integer contractAndSNSuccess;

    @Column(name = "optical_diff_success")
    private Integer opticalDiffSuccess;
} 