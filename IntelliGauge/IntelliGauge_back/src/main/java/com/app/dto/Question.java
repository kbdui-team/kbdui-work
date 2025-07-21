package com.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Question {
    private String question;
    private List<String> options;
    private String answer; // A/B/C/D
    // private String correctAnswer; // 添加correctAnswer字段以兼容Deepseek API
}
