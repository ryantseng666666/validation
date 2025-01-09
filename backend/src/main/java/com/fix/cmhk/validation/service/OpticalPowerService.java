package com.fix.cmhk.validation.service;

import com.fix.cmhk.validation.model.OpticalPowerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Slf4j
@Service
public class OpticalPowerService {
    
    public OpticalPowerResponse predictPower(String imageBase64) {
        log.info("Processing optical power prediction");
        // TODO: 实现实际的图像识别逻辑
        // 这里暂时返回模拟数据
        OpticalPowerResponse response = new OpticalPowerResponse();
        Random random = new Random();
        
        // 生成-30到0之间的随机光功率值
        response.setPowerValue(-30 + random.nextDouble() * 30);
        response.setMeasurementTime(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        response.setDeviceModel("OPM-" + String.format("%03d", random.nextInt(1000)));
        response.setStatus(response.getPowerValue() > -20 ? "NORMAL" : "WARNING");
        
        log.info("Optical power prediction completed");
        return response;
    }
} 