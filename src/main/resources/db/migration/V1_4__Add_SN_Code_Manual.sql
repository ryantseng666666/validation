use cmhk_validation;

ALTER TABLE order_info
ADD COLUMN sn_code_manual VARCHAR(10) NULL COMMENT '分包商设置的ONTSN码'; 