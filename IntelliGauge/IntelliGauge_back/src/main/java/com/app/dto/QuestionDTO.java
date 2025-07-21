package com.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class QuestionDTO {
    private Integer id;  // id, 主键
    private Integer lectureId; // lecture_id, 讲座ID
    private Integer contentInputId; // content_input_id, 内容输入ID
    private String questionType;  // question_type, 题目类型
    private String questionText;  // question_text, 题目文本
} 