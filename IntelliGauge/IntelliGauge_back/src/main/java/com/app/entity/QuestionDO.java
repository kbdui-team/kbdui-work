package com.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "questions")
public class QuestionDO {
    @TableId(type = IdType.AUTO)
    private Integer id;  // id, 主键

    private Integer lectureId; // lecture_id, 讲座ID
    private Integer contentInputId; // content_input_id, 内容输入ID
    private String questionType;  // question_type, 题目类型
    private String questionText;  // question_text, 题目文本
} 