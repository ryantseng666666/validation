use cmhk_validation;

ALTER TABLE order_info
ADD COLUMN upload_speed_success INT NULL COMMENT '上传速度是否达标，0不达标，1达标',
ADD COLUMN download_speed_success INT NULL COMMENT '下载速度是否达标，0不达标，1达标',
ADD COLUMN contract_and_sn_success INT NULL COMMENT '合同号和SN是否达标，0不达标，1达标'; 