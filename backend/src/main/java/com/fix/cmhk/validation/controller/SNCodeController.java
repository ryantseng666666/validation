package com.fix.cmhk.validation.controller;

import com.fix.cmhk.validation.model.PredictionRequest;
import com.fix.cmhk.validation.model.SNCodeResponse;
import com.fix.cmhk.validation.service.SNCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sn-code")
@RequiredArgsConstructor
public class SNCodeController {

    private final SNCodeService snCodeService;

    @PostMapping("/predict")
    public ResponseEntity<SNCodeResponse> predict(@RequestBody PredictionRequest request) {
        SNCodeResponse response = snCodeService.predictSNCode(request.getImage());
        return ResponseEntity.ok(response);
    }
} 