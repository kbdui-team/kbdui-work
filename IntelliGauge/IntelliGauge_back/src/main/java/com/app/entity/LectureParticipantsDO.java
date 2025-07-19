package com.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName(value = "lecture_participants")
public class LectureParticipantsDO {
    @TableId(type = IdType.AUTO)
    private Integer id;  // id, 主键

    private Integer lectureId; // lecture_id, 讲座ID
    private Integer userId; // user_id, 用户ID
    private String role;  // role, 角色
    private String anonymous;  // anonymous, 是否匿名
}
