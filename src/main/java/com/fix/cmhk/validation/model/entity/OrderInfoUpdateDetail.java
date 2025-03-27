package com.fix.cmhk.validation.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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