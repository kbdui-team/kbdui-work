package com.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserPreferencesDTO {
    private Integer id;  // id, 主键

    private Integer userId; // user_id, 用户ID
    private String allowAnonymous;  // allow_anonymous, 是否允许匿名
} 