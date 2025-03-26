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
    
    @Column(name = "download_speed")
    private String downloadSpeed;
    
    @Column(name = "download_speed_manual")
    private String downloadSpeedManual;
    
    @Column(name = "upload_speed")
    private String uploadSpeed;
    
    @Column(name = "upload_speed_manual")
    private String uploadSpeedManual;
    
    @Column(name = "fm_output_power")
    private String fmOutputPower;
    
    @Column(name = "odb_power_meter")
    private String odbPowerMeter;
    
    @Column(name = "fm_output_power_snapshot")
    private String fmOutputPowerSnapshot;
    
    @Column(name = "fm_output_power_manual")
    private String fmOutputPowerManual;
    
    @Column(name = "odb_power_meter_snapshot")
    private String odbPowerMeterSnapshot;
    
    @Column(name = "odb_power_meter_manual")
    private String odbPowerMeterManual;
    
    @Column(name = "ont_led_status")
    private String ontLedStatus;
    
    @Column(name = "ont_led_light_manual")
    private String ontLedLightManual;
    
    @Column(name = "line_label_manual")
    private String lineLabelManual;
    
    @Column(name = "ont_label_manual")
    private String ontLabelManual;
    
    @Column(name = "signed_uat")
    private String signedUat;
    
    @Column(name = "before_activity_photo")
    private String beforeActivityPhoto;
    
    @Column(name = "floor_difference")
    private String floorDifference;
    
    private String currentHandler;
    @Column(name = "quality_status")
    private String qualityStatus;
    
    @Column(name = "quality_remark")
    private String qualityRemark;
    
    @Column(name = "optical_power_auto_result")
    private String opticalPowerAutoResult;
    
    @Column(name = "optical_power_manual_result")
    private String opticalPowerManualResult;
    
    @Column(name = "speed_auto_result")
    private String speedAutoResult;
    
    @Column(name = "speed_manual_result")
    private String speedManualResult;
    
    private String isCharge;
    private String wifiPlan;
    private String sVlan;
    private String cVlan;
    
    @Column(name = "speed_test_ref_no")
    private String speedTestRefNo;
    
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

    @Column(name = "optical_power")
    private String opticalPower;
    
    @Column(name = "auto_success")
    private Integer autoSuccess;

    @Column(name = "is_ai_processed")
    private Integer isAIProcessed;

    @Column(name = "sn_code_snapshot")
    private String snCodeSnapshot;
    
    @Column(name = "contract_id_snapshot")
    private String contractIdSnapshot;
    
    @Column(name = "speed_test_ip_duplicate")
    private Integer speedTestIpDuplicate;
    
    @Column(name = "speed_test_ref_duplicate")
    private Integer speedTestRefIsDuplicate;
    
    @Column(name = "sn_duplicate")
    private Integer snIsDuplicate;
    
    @Column(name = "contract_id_duplicate")
    private Integer contractIdIsDuplicate;
    
    @Column(name = "item_status")
    private String itemStatus;
} 