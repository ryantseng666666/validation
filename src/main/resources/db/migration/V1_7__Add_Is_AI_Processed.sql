use cmhk_validation;

ALTER TABLE order_info
ADD COLUMN is_ai_processed INT DEFAULT 0 COMMENT '是否经过AI质检，0没有经过AI质检，1经过AI质检'; 