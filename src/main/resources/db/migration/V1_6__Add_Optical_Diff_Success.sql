use cmhk_validation;

ALTER TABLE order_info
ADD COLUMN optical_diff_success INT NULL COMMENT '光功率是否达标，0不达标，1达标'; 