package com.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName(value = "answer_history")
public class AnswerHistoryDO {
    @TableId(type = IdType.AUTO)
    private Integer id; // 主键
    private Integer userId; // 用户ID
    private Integer questionId; // 题目ID
    private String userAnswer; // 用户答案
    private Integer isCorrect; // 是否正确
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp answerTime; // 回答时间
} 