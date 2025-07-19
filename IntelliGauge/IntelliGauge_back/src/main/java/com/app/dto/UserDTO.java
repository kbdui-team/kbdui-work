package com.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserDTO {
    private Integer id;  // id, 主键

    private String userName; // user_name, 用户名
    private String realName; // real_name, 真实姓名
    private String email;  // email, 邮箱
    private String phone;  // phone, 电话
    private String status;  // status, 状态
    private String password;  // password, 密码
    private String userType;  // user_type, 用户类型
} 