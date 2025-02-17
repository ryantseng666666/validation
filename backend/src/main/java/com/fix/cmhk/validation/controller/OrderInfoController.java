package com.fix.cmhk.validation.controller;

import com.fix.cmhk.validation.model.entity.OrderInfo;
import com.fix.cmhk.validation.model.dto.DuplicateCheckResponse;
import com.fix.cmhk.validation.service.OrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderInfoController {

    @Autowired
    private OrderInfoService orderInfoService;

    @PostMapping
    public ResponseEntity<OrderInfo> createOrder(@RequestBody OrderInfo orderInfo) {
        OrderInfo createdOrder = orderInfoService.createOrder(orderInfo);
        return ResponseEntity.ok(createdOrder);
    }

    @PutMapping("/{jobNo}")
    public ResponseEntity<OrderInfo> updateOrder(
            @PathVariable String jobNo,
            @RequestBody OrderInfo orderInfo) {
        OrderInfo updatedOrder = orderInfoService.updateOrder(jobNo, orderInfo);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{jobNo}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String jobNo) {
        orderInfoService.deleteOrder(jobNo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{jobNo}")
    public ResponseEntity<OrderInfo> getOrderByJobNo(@PathVariable String jobNo) {
        return orderInfoService.findByJobNo(jobNo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/contract/{contractId}")
    public ResponseEntity<OrderInfo> getOrderByContractId(@PathVariable String contractId) {
        return orderInfoService.findByContractId(contractId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderInfo>> getOrdersByCustomerId(@PathVariable String customerId) {
        List<OrderInfo> orders = orderInfoService.findByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/customer/{customerId}/page")
    public ResponseEntity<Page<OrderInfo>> getOrdersByCustomerIdPaged(
            @PathVariable String customerId,
            Pageable pageable) {
        Page<OrderInfo> orders = orderInfoService.findByCustomerId(customerId, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/contract/{contractId}/page")
    public ResponseEntity<Page<OrderInfo>> getOrdersByContractIdPaged(
            @PathVariable String contractId,
            Pageable pageable) {
        Page<OrderInfo> orders = orderInfoService.findByContractId(contractId, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping
    public ResponseEntity<Page<OrderInfo>> getAllOrders(Pageable pageable) {
        Page<OrderInfo> orders = orderInfoService.findAll(pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/yesterday")
    public ResponseEntity<List<OrderInfo>> getYesterdayOrders() {
        List<OrderInfo> orders = orderInfoService.findYesterdayOrders();
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/last-month")
    public ResponseEntity<List<OrderInfo>> getLastMonthOrders() {
        List<OrderInfo> orders = orderInfoService.findLastMonthOrders();
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/last-year")
    public ResponseEntity<List<OrderInfo>> getLastYearOrders() {
        List<OrderInfo> orders = orderInfoService.findLastYearOrders();
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/yesterday/page")
    public ResponseEntity<Page<OrderInfo>> getYesterdayOrdersPaged(Pageable pageable) {
        Page<OrderInfo> orders = orderInfoService.findYesterdayOrders(pageable);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/last-month/page")
    public ResponseEntity<Page<OrderInfo>> getLastMonthOrdersPaged(Pageable pageable) {
        Page<OrderInfo> orders = orderInfoService.findLastMonthOrders(pageable);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/last-year/page")
    public ResponseEntity<Page<OrderInfo>> getLastYearOrdersPaged(Pageable pageable) {
        Page<OrderInfo> orders = orderInfoService.findLastYearOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/speed-test/ref/{refNo}")
    public ResponseEntity<OrderInfo> getBySpeedTestRefNo(@PathVariable Integer refNo) {
        return orderInfoService.findBySpeedTestRefNo(refNo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/speed-test/ip/{ip}")
    public ResponseEntity<List<OrderInfo>> getBySpeedTestIP(@PathVariable String ip) {
        List<OrderInfo> orders = orderInfoService.findBySpeedTestIP(ip);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/sn-code/{code}")
    public ResponseEntity<OrderInfo> getBySnCode(@PathVariable String code) {
        return orderInfoService.findBySnCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/ocr-contract/{id}")
    public ResponseEntity<OrderInfo> getByOcrContractId(@PathVariable String id) {
        return orderInfoService.findByOcrContractId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/check/speed-test-ip/{ip}")
    public ResponseEntity<DuplicateCheckResponse> checkSpeedTestIPDuplicate(@PathVariable String ip) {
        DuplicateCheckResponse response = orderInfoService.checkSpeedTestIPDuplicate(ip);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/check/speed-test-ref/{refNo}")
    public ResponseEntity<DuplicateCheckResponse> checkSpeedTestRefNoDuplicate(@PathVariable Integer refNo) {
        DuplicateCheckResponse response = orderInfoService.checkSpeedTestRefNoDuplicate(refNo);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/check/sn-code/{code}")
    public ResponseEntity<DuplicateCheckResponse> checkSnCodeDuplicate(@PathVariable String code) {
        DuplicateCheckResponse response = orderInfoService.checkSnCodeDuplicate(code);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/check/ocr-contract/{id}")
    public ResponseEntity<DuplicateCheckResponse> checkOcrContractIdDuplicate(@PathVariable String id) {
        DuplicateCheckResponse response = orderInfoService.checkOcrContractIdDuplicate(id);
        return ResponseEntity.ok(response);
    }
} 