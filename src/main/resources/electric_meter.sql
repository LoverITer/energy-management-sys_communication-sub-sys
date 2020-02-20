CREATE TABLE `electric_meter`  (
  `electric_meter_id` int(11) NOT NULL  COMMENT '电表编号',
  `user_id` int(11) NOT NULL COMMENT '用户编号',
  `user_balance` decimal(12, 2)  DEFAULT 0.00 COMMENT '用户当前账户余额,单位/元',
  `electricity_ip` varchar(256) DEFAULT NULL COMMENT '电表最近和服务器交互的ip',
  `electricity_price` decimal(6, 4)  DEFAULT 0.4983 COMMENT '电价,单位/元',
  `electricity_area`  varchar(256) default NULL COMMENT '电表的位置',
  `current_total_electricity` double(12, 2)  DEFAULT NULL COMMENT '用户当前（包括尖，峰，平，谷）总用电量，单位KW/h',
  `current_total_super_peak_electricity` double(12, 2) NULL DEFAULT NULL COMMENT '用户当前超高峰（尖）总用电量，单位KW/h',
  `current_total_peak_electricity` double(12, 2)  DEFAULT NULL COMMENT '用户当前高峰（峰）总用电量，单位KW/h',
  `current_total_normal_electricity` double(12, 2)  DEFAULT NULL COMMENT '用户当前正常时间（平）总用电量，单位KW/h',
  `current_total_valley_electricity` double(12, 2)  DEFAULT NULL COMMENT '用户当前低谷（谷）总用电量，单位KW/h',
  PRIMARY KEY (`electric_meter_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;
