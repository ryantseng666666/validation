package com.fix.cmhk.validation.repository;

import com.fix.cmhk.validation.model.entity.OrderInfoUpdateDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderInfoUpdateDetailRepository extends JpaRepository<OrderInfoUpdateDetail, Long> {
    List<OrderInfoUpdateDetail> findByJobNoOrderByUpdateTimeDesc(String jobNo);
} 