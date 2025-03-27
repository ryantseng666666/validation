/*
 Navicat Premium Data Transfer

 Source Server         : 沈阳_综调
 Source Server Type    : MySQL
 Source Server Version : 80034
 Source Host           : 10.108.1.25:13306
 Source Schema         : fsp_hbim

 Target Server Type    : MySQL
 Target Server Version : 80034
 File Encoding         : 65001

 Date: 08/01/2025 11:26:59
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for order_info
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `job_no` varchar(50) NOT NULL COMMENT '工单号',
  `contract_id` varchar(50) DEFAULT NULL COMMENT '合同号',
  `title` TEXT DEFAULT NULL COMMENT '标题',
  `order_type` varchar(50) DEFAULT NULL COMMENT '工单类型',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `customer_order_id` varchar(50) DEFAULT NULL COMMENT '客户工单号',
  `customer_sub_order_id` varchar(50) DEFAULT NULL COMMENT '客户子工单号',
  `rate_plan` varchar(50) DEFAULT NULL COMMENT '资费计划',
  `service_type` varchar(50) DEFAULT NULL COMMENT '服务类型',
  `order_status` varchar(50) DEFAULT NULL COMMENT '工单状态',
  `current_link` varchar(50) DEFAULT NULL COMMENT '当前环节',
  `appointment_date` datetime DEFAULT NULL COMMENT '预约时间',
  `arrive_time` datetime DEFAULT NULL COMMENT '到达时间',
  `archive_time` datetime DEFAULT NULL COMMENT '归档时间',
  `worker_id` varchar(50) DEFAULT NULL COMMENT '工程师ID',
  `worker_name` varchar(50) DEFAULT NULL COMMENT '工程师姓名',
  `worker_partner` varchar(50) DEFAULT NULL COMMENT '工程师合作伙伴',
  `result_code` varchar(50) DEFAULT NULL COMMENT '结果代码',
  `result_code_desc` TEXT DEFAULT NULL COMMENT '结果描述',
  `customer_id` varchar(50) DEFAULT NULL COMMENT '客户ID',
  `customer` varchar(100) DEFAULT NULL COMMENT '客户名称',
  `customer_contact` varchar(50) DEFAULT NULL COMMENT '客户联系方式',
  `customer_address` TEXT DEFAULT NULL COMMENT '客户地址',
  `address_code` varchar(50) DEFAULT NULL COMMENT '地址编码',
  `area` varchar(50) DEFAULT NULL COMMENT '区域',
  `region` varchar(50) DEFAULT NULL COMMENT '地区',
  `district` varchar(50) DEFAULT NULL COMMENT '行政区',
  `cmhk_building_id` varchar(50) DEFAULT NULL COMMENT '中国移动香港大厦ID',
  `build_name` TEXT DEFAULT NULL COMMENT '大厦名称',
  `business_type` varchar(50) DEFAULT NULL COMMENT '业务类型',
  `task_category` varchar(50) DEFAULT NULL COMMENT '任务类别',
  `task_sub_category` varchar(50) DEFAULT NULL COMMENT '任务子类别',
  `bandwidth` varchar(50) DEFAULT NULL COMMENT '带宽',
  `splitter_id` varchar(50) DEFAULT NULL COMMENT '分光器ID',
  `splitting_ratio` varchar(50) DEFAULT NULL COMMENT '分光比',
  `fm_id` varchar(50) DEFAULT NULL COMMENT 'FM ID',
  `fm_port_no` varchar(50) DEFAULT NULL COMMENT 'FM端口号',
  `fm_name` varchar(100) DEFAULT NULL COMMENT 'FM名称',
  `fm_carrier` varchar(50) DEFAULT NULL COMMENT 'FM运营商',
  `facility_type` varchar(50) DEFAULT NULL COMMENT '设施类型',
  `pre_wiring` varchar(50) DEFAULT NULL COMMENT '预布线',
  `pon_port` varchar(50) DEFAULT NULL COMMENT 'PON端口',
  `olt_name` varchar(100) DEFAULT NULL COMMENT 'OLT名称',
  `olt_vendor` varchar(50) DEFAULT NULL COMMENT 'OLT厂商',
  `olt_type` varchar(50) DEFAULT NULL COMMENT 'OLT类型',
  `olt_ip` varchar(50) DEFAULT NULL COMMENT 'OLT IP',
  `olt_carrier` varchar(50) DEFAULT NULL COMMENT 'OLT运营商',
  `ont_vendor` varchar(50) DEFAULT NULL COMMENT 'ONT厂商',
  `ont_old_sn` varchar(50) DEFAULT NULL COMMENT 'ONT旧序列号',
  `ont_new_sn` varchar(50) DEFAULT NULL COMMENT 'ONT新序列号',
  `router_type` varchar(50) DEFAULT NULL COMMENT '路由器类型',
  `router_sn` varchar(50) DEFAULT NULL COMMENT '路由器序列号',
  `olt_tx_opt_power` TEXT DEFAULT NULL COMMENT 'OLT发送光功率',
  `ont_rx_opt_power` TEXT DEFAULT NULL COMMENT 'ONT接收光功率',
  `ont_tx_opt_power` TEXT DEFAULT NULL COMMENT 'ONT发送光功率',
  `olt_rx_opt_power` TEXT DEFAULT NULL COMMENT 'OLT接收光功率',
  `speed_test_result` TEXT DEFAULT NULL COMMENT '速度测试结果',
  `download_speed` TEXT DEFAULT NULL COMMENT '下载速度',
  `download_speed_manual` TEXT DEFAULT NULL COMMENT '手动下载速度',
  `upload_speed` TEXT DEFAULT NULL COMMENT '上传速度',
  `upload_speed_manual` TEXT DEFAULT NULL COMMENT '手动上传速度',
  `fm_output_power_snapshot` TEXT DEFAULT NULL COMMENT 'FM输出功率快照',
  `fm_output_power` TEXT DEFAULT NULL COMMENT 'FM输出功率',
  `fm_output_power_manual` TEXT DEFAULT NULL COMMENT '手动FM输出功率',
  `odb_power_meter_snapshot` TEXT DEFAULT NULL COMMENT 'ODB功率计快照',
  `odb_power_meter` TEXT DEFAULT NULL COMMENT 'ODB功率计',
  `odb_power_meter_manual` TEXT DEFAULT NULL COMMENT '手动ODB功率计',
  `ont_led_status` TEXT DEFAULT NULL COMMENT 'ONT LED状态',
  `ont_led_light_manual` TEXT DEFAULT NULL COMMENT '手动ONT LED灯',
  `line_label_manual` TEXT DEFAULT NULL COMMENT '手动线路标签',
  `ont_label_manual` TEXT DEFAULT NULL COMMENT '手动ONT标签',
  `signed_uat` TEXT DEFAULT NULL COMMENT '签署UAT',
  `before_activity_photo` TEXT DEFAULT NULL COMMENT '活动前照片',
  `floor_difference` TEXT DEFAULT NULL COMMENT '楼层差异',
  `current_handler` varchar(50) DEFAULT NULL COMMENT '当前处理人',
  `item_status` varchar(50) DEFAULT NULL COMMENT '项目状态',
  `quality_status` varchar(50) DEFAULT NULL COMMENT '质量状态',
  `quality_remark` TEXT DEFAULT NULL COMMENT '质量备注',
  `optical_power_auto_result` TEXT DEFAULT NULL COMMENT '光功率自动结果',
  `optical_power_manual_result` TEXT DEFAULT NULL COMMENT '光功率手动结果',
  `speed_auto_result` TEXT DEFAULT NULL COMMENT '速度自动结果',
  `speed_manual_result` TEXT DEFAULT NULL COMMENT '速度手动结果',
  `is_charge` varchar(10) DEFAULT NULL COMMENT '是否收费',
  `wifi_plan` varchar(50) DEFAULT NULL COMMENT 'WiFi计划',
  `s_vlan` varchar(50) DEFAULT NULL COMMENT 'S-VLAN',
  `c_vlan` varchar(50) DEFAULT NULL COMMENT 'C-VLAN',
  PRIMARY KEY (`job_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单信息表';

SET FOREIGN_KEY_CHECKS = 1;
