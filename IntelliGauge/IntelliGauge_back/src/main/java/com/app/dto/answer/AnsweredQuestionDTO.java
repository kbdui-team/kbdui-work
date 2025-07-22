package com.app.dto.answer;

import com.app.dto.QuestionDTO;
import com.app.dto.QuestionOptionsDTO;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class AnsweredQuestionDTO {
    private String userAnswer;
    private Integer isCorrect;
    private Timestamp answerTime;
    private QuestionDTO questionDTO;
    private List<QuestionOptionsDTO> questionOptionsDTOS;
}
