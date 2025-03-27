ALTER TABLE order_info
ADD COLUMN admin_upload_speed VARCHAR(255) NULL COMMENT '管理员设置上传速度',
ADD COLUMN admin_download_speed VARCHAR(255) NULL COMMENT '管理员设置下传速度',
ADD COLUMN admin_socket_optical_power VARCHAR(255) NULL COMMENT '管理员设置的插座光功率',
ADD COLUMN admin_fm_optical_power VARCHAR(255) NULL COMMENT '管理员设置的FM光功率',
ADD COLUMN admin_contract_id VARCHAR(10) NULL COMMENT '管理员设置的ONT合同号',
ADD COLUMN admin_sn VARCHAR(10) NULL COMMENT '管理员设置的ONTSN码'; 