package com.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "lectures")
public class LectureDO {
    @TableId(type = IdType.AUTO)
    private Integer id;  // id, 主键

    private String title; // title, 标题
    private String description;  // description, 描述
    private Integer teacherId; // teacher_id, 教师ID
    private String status;  // status, 状态
} 