package com.app.dto.answer;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class AnswerHistoryDTO {
    private Integer id;
    private Integer userId;
    private Integer questionId;
    private String userAnswer;
    private Integer isCorrect;
    private Timestamp answerTime;
} 