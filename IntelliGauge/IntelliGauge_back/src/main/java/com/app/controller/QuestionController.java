package com.app.controller;

import com.app.dto.QuestionDTO;
import com.app.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/question")
@Slf4j
@CrossOrigin
public class QuestionController {
    @Resource
    private QuestionService questionService;

    /**
     * 添加题目信息
     */
    @PostMapping("/add")
    public ResponseEntity<QuestionDTO> addQuestion(@RequestBody QuestionDTO question) {
        System.out.println("添加题目aaa："+question);
        return questionService.addQuestion(question);
    }

    /**
     * 根据ID删除题目
     */
    @DeleteMapping("/delete/{id}")
    public boolean deleteQuestionById(@PathVariable Integer id) {
        return questionService.deleteQuestionById(id);
    }

    /**
     * 批量删除题目
     */
    @DeleteMapping("/delete/batch")
    public boolean deleteQuestionsByIds(@RequestBody List<Integer> ids) {
        return questionService.deleteQuestionsByIds(ids);
    }

    /**
     * 更新题目信息
     */
    @PutMapping("/update")
    public boolean updateQuestion(@RequestBody QuestionDTO question) {
        return questionService.updateQuestion(question);
    }

    /**
     * 根据ID查询题目
     */
    @GetMapping("/get/{id}")
    public QuestionDTO getQuestionById(@PathVariable Integer id) {
        return questionService.getQuestionById(id);
    }

    /**
     * 查询所有题目
     */
    @GetMapping("/list")
    public List<QuestionDTO> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    /**
     * 分页查询题目
     */
    @PostMapping("/page")
    public Object getAllQuestionsByPage(@RequestParam(defaultValue = "1") Integer pageNo,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestBody Map<String, Object> conditions) {
        return questionService.getAllQuestionsByPage(pageNo, pageSize, conditions);
    }
} 