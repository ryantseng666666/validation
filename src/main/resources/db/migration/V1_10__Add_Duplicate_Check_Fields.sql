use cmhk_validation;

ALTER TABLE order_info
ADD COLUMN speed_test_ref_is_duplicate INT NULL COMMENT '是否速度测试参考编号重复，0不重复，1重复',
ADD COLUMN speed_test_ip_duplicate INT NULL COMMENT '是否速度测试IP重复，0不重复，1重复',
ADD COLUMN sn_is_duplicate INT NULL COMMENT '是否sn码重复，0不重复，1重复',
ADD COLUMN contract_id_is_duplicate INT NULL COMMENT '是否合同编号重复，0不重复，1重复'; 