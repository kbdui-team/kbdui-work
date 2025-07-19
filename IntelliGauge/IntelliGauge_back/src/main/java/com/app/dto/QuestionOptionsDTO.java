package com.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class QuestionOptionsDTO {
    private Integer id;  // id, 主键

    private Integer questionId; // question_id, 题目ID
    private String optionText;  // option_text, 选项文本
    private String optionOrder;  // option_order, 选项顺序
    private String isCorrect;  // is_correct, 是否正确答案
} 