package com.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class LectureParticipantsDTO {
    private Integer id;  // id, 主键

    private Integer lectureId; // lecture_id, 讲座ID
    private Integer userId; // user_id, 用户ID
    private String role;  // role, 角色
    private String anonymous;  // anonymous, 是否匿名
} 