use  cmhk_validation;
CREATE TABLE order_info_update_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_no VARCHAR(255) NOT NULL COMMENT '工单号',
    field_name VARCHAR(50) NOT NULL COMMENT '修改的字段名',
    old_value VARCHAR(255) COMMENT '原值',
    new_value VARCHAR(255) COMMENT '新值',
    update_time DATETIME NOT NULL COMMENT '修改时间',
    update_by VARCHAR(50) NOT NULL COMMENT '修改人',
    FOREIGN KEY (job_no) REFERENCES order_info(job_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单修改明细表';

CREATE INDEX idx_job_no ON order_info_update_detail(job_no); 