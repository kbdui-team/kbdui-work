package com.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName(value = "user_preferences")
public class UserPreferencesDO {
    @TableId(type = IdType.AUTO)
    private Integer id;  // id, 主键

    private Integer userId; // user_id, 用户ID
    private String allowAnonymous;  // allow_anonymous, 是否允许匿名
}
