package com.fix.cmhk.validation.controller;

import com.fix.cmhk.validation.model.PredictionRequest;
import com.fix.cmhk.validation.model.SpeedTestResponse;
import com.fix.cmhk.validation.service.SpeedTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/speedtest")
@RequiredArgsConstructor
public class SpeedTestController {

    private final SpeedTestService speedTestService;

    @PostMapping("/predict")
    public ResponseEntity<SpeedTestResponse> predict(@RequestBody PredictionRequest request) {
        SpeedTestResponse response = speedTestService.predictSpeed(request.getImage());
        return ResponseEntity.ok(response);
    }
} 