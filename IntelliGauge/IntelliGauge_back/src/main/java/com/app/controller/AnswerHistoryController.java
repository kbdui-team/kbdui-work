package com.app.controller;

import com.app.dto.answer.AnswerHistoryDTO;
import com.app.dto.answer.StudentAnswerDTO;
import com.app.dto.answer.StudentAnswerQueryRequest;
import com.app.service.AnswerHistoryService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/answerHistory")
public class AnswerHistoryController {
    @Resource
    private AnswerHistoryService answerHistoryService;

    /**
     * 新增答题记录
     */
    @PostMapping("/add")
    public boolean add(@RequestBody AnswerHistoryDTO dto) {
        return answerHistoryService.addAnswerHistory(dto);
    }

    /**
     * 删除指定ID的答题记录
     */
    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable Integer id) {
        return answerHistoryService.deleteAnswerHistoryById(id);
    }

    /**
     * 批量删除答题记录
     */
    @PostMapping("/deleteBatch")
    public boolean deleteBatch(@RequestBody List<Integer> ids) {
        return answerHistoryService.deleteAnswerHistoriesByIds(ids);
    }

    /**
     * 更新答题记录
     */
    @PutMapping("/update")
    public boolean update(@RequestBody AnswerHistoryDTO dto) {
        return answerHistoryService.updateAnswerHistory(dto);
    }

    /**
     * 根据ID查询答题记录
     */
    @GetMapping("/get/{id}")
    public AnswerHistoryDTO getById(@PathVariable Integer id) {
        return answerHistoryService.getAnswerHistoryById(id);
    }

    /**
     * 查询所有答题记录
     */
    @GetMapping("/list")
    public List<AnswerHistoryDTO> getAll() {
        return answerHistoryService.getAllAnswerHistories();
    }

    /**
     * 分页查询答题记录
     */
    @PostMapping("/page")
    public Page<AnswerHistoryDTO> getPage(@RequestParam Integer pageNo,
                                          @RequestParam Integer pageSize,
                                          @RequestBody(required = false) Map<String, Object> conditions) {
        if (conditions == null) conditions = new java.util.HashMap<>();
        return answerHistoryService.getAllAnswerHistoriesByPage(pageNo, pageSize, conditions);
    }

    /**
     * 查询指定学生的所有答题情况（分页）
     */@PostMapping("/student/{userId}/answers/query")
    public StudentAnswerDTO getStudentAnswers(
            @PathVariable Integer userId,
            @RequestBody  StudentAnswerQueryRequest request) {
        return answerHistoryService.getAllAnswerHistoriesForStudent(
                request.getPageNo(),
                request.getPageSize(),
                userId,
                request.getLectureId());
    }
} 