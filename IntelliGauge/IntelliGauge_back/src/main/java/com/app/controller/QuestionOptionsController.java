package com.app.controller;

import com.app.dto.QuestionOptionsDTO;
import com.app.service.QuestionOptionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/question-options")
@Slf4j
@CrossOrigin
public class QuestionOptionsController {
    @Resource
    private QuestionOptionsService questionOptionsService;

    /**
     * 添加题目选项信息
     */
    @PostMapping("/add")
    public boolean addQuestionOptions(@RequestBody QuestionOptionsDTO questionOptions) {
        System.out.println("添加题目："+questionOptions);
        return questionOptionsService.addQuestionOptions(questionOptions);
    }

    /**
     * 根据ID删除题目选项
     */
    @DeleteMapping("/delete/{id}")
    public boolean deleteQuestionOptionsById(@PathVariable Integer id) {
        return questionOptionsService.deleteQuestionOptionsById(id);
    }

    /**
     * 批量删除题目选项
     */
    @DeleteMapping("/delete/batch")
    public boolean deleteQuestionOptionsByIds(@RequestBody List<Integer> ids) {
        return questionOptionsService.deleteQuestionOptionsByIds(ids);
    }

    /**
     * 更新题目选项信息
     */
    @PutMapping("/update")
    public boolean updateQuestionOptions(@RequestBody QuestionOptionsDTO questionOptions) {
        return questionOptionsService.updateQuestionOptions(questionOptions);
    }

    /**
     * 根据ID查询题目选项
     */
    @GetMapping("/get/{id}")
    public QuestionOptionsDTO getQuestionOptionsById(@PathVariable Integer id) {
        return questionOptionsService.getQuestionOptionsById(id);
    }

    /**
     * 查询所有题目选项
     */
    @GetMapping("/list")
    public List<QuestionOptionsDTO> getAllQuestionOptions() {
        return questionOptionsService.getAllQuestionOptions();
    }

    /**
     * 分页查询题目选项
     */
    @PostMapping("/page")
    public Object getAllQuestionOptionsByPage(@RequestParam(defaultValue = "1") Integer pageNo,
                                             @RequestParam(defaultValue = "10") Integer pageSize,
                                             @RequestBody Map<String, Object> conditions) {
        return questionOptionsService.getAllQuestionOptionsByPage(pageNo, pageSize, conditions);
    }
} 