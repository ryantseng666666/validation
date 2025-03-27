package com.fix.cmhk.validation.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Service
@Slf4j
public class OrderInfoServiceImpl implements OrderInfoService {

    @Autowired
    private OrderInfoRepository orderInfoRepository;
    
    @Autowired
    private OpticalPowerService opticalPowerService;
    
    @Autowired
    private SpeedTestService speedTestService;
    
    @Autowired
    private ContractService contractService;

    @Override
    @Transactional
    public boolean processMonthlyAIInspection(Integer year, Integer month) {
        try {
            List<OrderInfo> orders = orderInfoRepository.findUnprocessedOrdersByMonth(year, month);
            
            for (OrderInfo order : orders) {
                int retryCount = 0;
                boolean success = false;
                
                while (retryCount < 3 && !success) {
                    try {
                        processOrder(order);
                        success = true;
                    } catch (Exception e) {
                        log.error("Error processing order {}, retry {}", order.getJobNo(), retryCount + 1, e);
                        retryCount++;
                        if (retryCount == 3) {
                            log.error("Failed to process order {} after 3 retries", order.getJobNo());
                            return false;
                        }
                        Thread.sleep(1000); // Wait before retry
                    }
                }
            }
            
            return true;
        } catch (Exception e) {
            log.error("Error in processMonthlyAIInspection", e);
            return false;
        }
    }

    private void processOrder(OrderInfo order) {
        // Process FM output power
        if (order.getFmOutputPowerSnapshot() != null) {
            OpticalPowerResult fmResult = opticalPowerService.predict(order.getFmOutputPowerSnapshot());
            order.setFmOutputPower(fmResult.getOpticalPower());
        }

        // Process ODB power meter
        if (order.getOdbPowerMeterSnapshot() != null) {
            OpticalPowerResult odbResult = opticalPowerService.predict(order.getOdbPowerMeterSnapshot());
            order.setOdbPowerMeter(odbResult.getOpticalPower());
        }

        // Process SN code
        if (order.getSnSnapshot() != null) {
            SNCodeResult snResult = contractService.predictSN(order.getSnSnapshot());
            order.setSnCode(snResult.getSnCode());
        }

        // Process contract ID
        if (order.getContractIdSnapshot() != null) {
            ContractResult contractResult = contractService.predict(order.getContractIdSnapshot());
            order.setOcrContractId(contractResult.getContractId());
        }

        // Process speed test
        if (order.getSpeedTestResult() != null) {
            SpeedTestResult speedResult = speedTestService.predict(order.getSpeedTestResult());
            order.setUploadSpeed(speedResult.getUploadSpeed());
            order.setDownloadSpeed(speedResult.getDownloadSpeed());
            order.setSpeedTestRefNo(speedResult.getReferenceId());
            order.setSpeedTestIP(speedResult.getIpAddress());
        }

        order.setIsAIProcessed(1);
        orderInfoRepository.save(order);
    }

    @Override
    @Transactional
    public boolean checkMonthlyDuplicates(Integer year, Integer month) {
        try {
            List<OrderInfo> orders = orderInfoRepository.findOrdersByMonth(year, month);
            
            for (OrderInfo order : orders) {
                // Check IP duplicates
                int ipCount = orderInfoRepository.countDuplicateIP(order.getSpeedTestIP());
                order.setSpeedTestIpDuplicate(ipCount > 1 ? 1 : 0);

                // Check reference number duplicates
                int refCount = orderInfoRepository.countDuplicateRefNo(order.getSpeedTestRefNo());
                order.setSpeedTestRefIsDuplicate(refCount > 1 ? 1 : 0);

                // Check SN code duplicates
                int snCount = orderInfoRepository.countDuplicateSN(order.getSnCode());
                order.setSnIsDuplicate(snCount > 1 ? 1 : 0);

                // Check contract ID duplicates
                int contractCount = orderInfoRepository.countDuplicateContractId(order.getOcrContractId());
                order.setContractIdIsDuplicate(contractCount > 1 ? 1 : 0);

                // Update contract and SN success status
                order.setContractAndSNSuccess(
                    order.getSnIsDuplicate() != 1 && order.getContractIdIsDuplicate() != 1 ? 1 : 0
                );

                // Process speed test success
                processSpeedTestSuccess(order);

                // Process optical power success
                processOpticalPowerSuccess(order);

                // Update quality status
                updateQualityStatus(order);

                orderInfoRepository.save(order);
            }
            
            return true;
        } catch (Exception e) {
            log.error("Error in checkMonthlyDuplicates", e);
            return false;
        }
    }

    private void processSpeedTestSuccess(OrderInfo order) {
        try {
            double bandwidth = Double.parseDouble(order.getBandwidth());
            double threshold = 0.8 * bandwidth;

            // Check upload speed
            double uploadSpeed = getEffectiveSpeed(order.getUploadSpeed(), order.getUploadSpeedManual());
            order.setUploadSpeedSuccess(uploadSpeed > threshold ? 1 : 0);

            // Check download speed
            double downloadSpeed = getEffectiveSpeed(order.getDownloadSpeed(), order.getDownloadSpeedManual());
            order.setDownloadSpeedSuccess(downloadSpeed > threshold ? 1 : 0);
        } catch (Exception e) {
            log.error("Error processing speed test success for order {}", order.getJobNo(), e);
            order.setUploadSpeedSuccess(-1);
            order.setDownloadSpeedSuccess(-1);
        }
    }

    private double getEffectiveSpeed(String primary, String backup) {
        try {
            if (primary != null && !primary.equals("-1")) {
                return Double.parseDouble(primary);
            }
            if (backup != null && !backup.equals("-1")) {
                return Double.parseDouble(backup);
            }
            return -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void processOpticalPowerSuccess(OrderInfo order) {
        try {
            double fmPower = getEffectivePower(order.getFmOutputPower(), order.getFmOutputPowerManual());
            double odbPower = getEffectivePower(order.getOdbPowerMeter(), order.getOdbPowerMeterManual());

            if (fmPower != -1 && odbPower != -1) {
                double diff = Math.abs(fmPower - odbPower);
                order.setOpticalDiffSuccess(diff <= 1.6 && odbPower <= -26 ? 1 : 0);
            } else {
                order.setOpticalDiffSuccess(-1);
            }
        } catch (Exception e) {
            log.error("Error processing optical power success for order {}", order.getJobNo(), e);
            order.setOpticalDiffSuccess(-1);
        }
    }

    private double getEffectivePower(String primary, String backup) {
        try {
            if (primary != null && !primary.equals("-1")) {
                return Double.parseDouble(primary);
            }
            if (backup != null && !backup.equals("-1")) {
                return Double.parseDouble(backup);
            }
            return -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void updateQualityStatus(OrderInfo order) {
        boolean allSuccess = 
            order.getSpeedTestIpDuplicate() == 1 &&
            order.getSpeedTestRefIsDuplicate() == 1 &&
            order.getSnIsDuplicate() == 1 &&
            order.getContractIdIsDuplicate() == 1 &&
            order.getUploadSpeedSuccess() == 1 &&
            order.getDownloadSpeedSuccess() == 1 &&
            order.getOpticalDiffSuccess() == 1 &&
            (order.getItemStatus() == null || order.getItemStatus().equals("Y"));

        order.setQualityStatus(allSuccess ? "autoSuccess" : "autoFail");
    }
} 