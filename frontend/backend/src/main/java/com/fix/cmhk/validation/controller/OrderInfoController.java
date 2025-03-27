package com.fix.cmhk.validation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderInfoController {

    @Autowired
    private OrderInfoService orderInfoService;

    @PostMapping("/process-monthly-ai-inspection")
    public ResponseEntity<?> processMonthlyAIInspection(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        try {
            boolean success = orderInfoService.processMonthlyAIInspection(year, month);
            return ResponseEntity.ok(new BaseResponse<>(success ? "success" : "fail"));
        } catch (Exception e) {
            log.error("Error processing monthly AI inspection", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>("fail", e.getMessage()));
        }
    }

    @PostMapping("/check-monthly-duplicates")
    public ResponseEntity<?> checkMonthlyDuplicates(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        try {
            boolean success = orderInfoService.checkMonthlyDuplicates(year, month);
            return ResponseEntity.ok(new BaseResponse<>(success ? "success" : "fail"));
        } catch (Exception e) {
            log.error("Error checking monthly duplicates", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>("fail", e.getMessage()));
        }
    }
} 