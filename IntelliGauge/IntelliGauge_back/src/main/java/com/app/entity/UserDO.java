package com.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName(value = "user")
public class UserDO {
    @TableId(type = IdType.AUTO)
    private Integer id;  // id, 主键

    private String userName; // user_name, 用户名
    private String realName; // real_name, 真实姓名
    private String email;  // email, 邮箱
    private String phone;  // phone, 电话
    private String status;  // status, 状态
    private String password;  // password, 密码
    private String userType;  // user_type, 用户类型
}
