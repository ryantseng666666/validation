package com.fix.cmhk.validation.model.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_info_update_detail")
public class OrderInfoUpdateDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String jobNo;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private LocalDateTime updateTime;
    private String updateBy;
} 