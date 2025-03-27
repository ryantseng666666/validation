use cmhk_validation;

ALTER TABLE order_info
ADD COLUMN contract_id_snapshot VARCHAR(255) NULL COMMENT 'contractID照片url'; 