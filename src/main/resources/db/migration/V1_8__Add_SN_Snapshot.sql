use cmhk_validation;

ALTER TABLE order_info
ADD COLUMN sn_snapshot VARCHAR(255) NULL COMMENT 'sn照片url'; 