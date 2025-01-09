package com.fix.cmhk.validation.controller;

import com.fix.cmhk.validation.model.PredictionRequest;
import com.fix.cmhk.validation.model.OpticalPowerResponse;
import com.fix.cmhk.validation.service.OpticalPowerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/optical-power")
@RequiredArgsConstructor
public class OpticalPowerController {

    private final OpticalPowerService opticalPowerService;

    @PostMapping("/predict")
    public ResponseEntity<OpticalPowerResponse> predict(@RequestBody PredictionRequest request) {
        OpticalPowerResponse response = opticalPowerService.predictPower(request.getImage());
        return ResponseEntity.ok(response);
    }
} 