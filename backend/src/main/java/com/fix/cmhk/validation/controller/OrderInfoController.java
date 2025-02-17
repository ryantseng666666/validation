package com.fix.cmhk.validation.controller;

import com.fix.cmhk.validation.model.entity.OrderInfo;
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
} 