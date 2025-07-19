package com.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class LectureDTO {
    private Integer id;  // id, 主键

    private String title; // title, 标题
    private String description;  // description, 描述
    private Integer teacherId; // teacher_id, 教师ID
    private String status;  // status, 状态
} 