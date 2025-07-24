package response;

import lombok.Data;

@Data
public class StudentLectureAnswerStatResponse {
    private int answeredCount;   // 学生已回答题数
    private int correctCount;    // 学生回答正确题数
    private int totalCount;      // 讲座下所有题目数
} 