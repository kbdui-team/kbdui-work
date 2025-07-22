package com.app.dto.answer;

import com.app.dto.UserDTO;
import lombok.Data;

import java.util.List;

// 查询学生的问题后返回该DTO
@Data
public class StudentAnswerDTO {
    private UserDTO userDTO;
    private List<AnsweredQuestionDTO> answeredQuestionDTOS;

}
