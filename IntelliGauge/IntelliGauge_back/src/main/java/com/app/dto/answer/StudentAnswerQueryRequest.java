package com.app.dto.answer;

import lombok.Data;

@Data
public class StudentAnswerQueryRequest {

    private  Integer pageNo;

    private  Integer pageSize;

    private  Integer lectureId;
}
