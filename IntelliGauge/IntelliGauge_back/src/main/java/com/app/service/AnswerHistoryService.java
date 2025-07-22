package com.app.service;

import com.app.dto.QuestionOptionsDTO;
import com.app.dto.answer.StudentAnswerDTO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.app.dao.AnswerHistoryDAO;
import com.app.dto.answer.AnswerHistoryDTO;
import com.app.entity.AnswerHistoryDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.app.service.UserService;
import com.app.service.QuestionService;
import com.app.dto.UserDTO;
import com.app.dto.QuestionDTO;
import com.app.dto.answer.AnsweredQuestionDTO;

@Service
@Slf4j
public class AnswerHistoryService {
    @Resource
    private AnswerHistoryDAO answerHistoryDAO;
    @Resource
    private UserService userService;
    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionOptionsService questionOptionsService;

    public boolean addAnswerHistory(AnswerHistoryDTO answerHistory) {
        AnswerHistoryDO entity = convertToEntity(answerHistory);
        return answerHistoryDAO.insert(entity) > 0;
    }

    public boolean deleteAnswerHistoryById(Integer id) {
        return answerHistoryDAO.deleteById(id) > 0;
    }

    public boolean deleteAnswerHistoriesByIds(List<Integer> ids) {
        return answerHistoryDAO.deleteBatchIds(ids) > 0;
    }

    public boolean updateAnswerHistory(AnswerHistoryDTO answerHistory) {
        AnswerHistoryDO entity = convertToEntity(answerHistory);
        return answerHistoryDAO.updateById(entity) > 0;
    }

    public AnswerHistoryDTO getAnswerHistoryById(Integer id) {
        AnswerHistoryDO entity = answerHistoryDAO.selectById(id);
        return convertToDTO(entity);
    }

    public List<AnswerHistoryDTO> getAllAnswerHistories() {
        List<AnswerHistoryDO> list = answerHistoryDAO.selectList(null);
        return list.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public Page<AnswerHistoryDTO> getAllAnswerHistoriesByPage(Integer pageNo, Integer pageSize, Map<String, Object> conditions) {
        Page<AnswerHistoryDO> page = new Page<>(pageNo, pageSize);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AnswerHistoryDO> queryWrapper = new QueryWrapper<>();
        conditions.forEach((key, value) -> {
            if (value != null && isFieldExists(AnswerHistoryDO.class, key)) {
                queryWrapper.like(camelToSnake(key), value);
            }
        });
        Page<AnswerHistoryDO> resultPage = answerHistoryDAO.selectPage(page, queryWrapper);
        List<AnswerHistoryDTO> records = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        Page<AnswerHistoryDTO> result = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        result.setRecords(records);
        return result;
    }

    public StudentAnswerDTO getAllAnswerHistoriesForStudent(Integer pageNo, Integer pageSize, Integer id) {
        // 获取学生信息
        UserDTO userDTO = userService.getUserById(id);
        // 查询该学生所有答题记录
        List<AnswerHistoryDO> answerHistoryList = answerHistoryDAO.selectList(
            new QueryWrapper<AnswerHistoryDO>().eq("user_id", id)
        );
        // 组装AnsweredQuestionDTO列表
        List<AnsweredQuestionDTO> answeredQuestionDTOS = answerHistoryList.stream().map(answerHistory -> {
            AnsweredQuestionDTO answered = new AnsweredQuestionDTO();
            answered.setUserAnswer(answerHistory.getUserAnswer());
            answered.setIsCorrect(answerHistory.getIsCorrect());
            answered.setAnswerTime(answerHistory.getAnswerTime());
            // 查询题目信息
            QuestionDTO questionDTO = questionService.getQuestionById(answerHistory.getQuestionId());
            answered.setQuestionDTO(questionDTO);
            // 查询问题选项信息
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("questionId", questionDTO.getId());
            List<QuestionOptionsDTO> questionOptionsDTOS = questionOptionsService.getAllQuestionOptionsByPage(1, 10, conditions).getRecords();
            answered.setQuestionOptionsDTOS(questionOptionsDTOS);
            return answered;
        }).collect(Collectors.toList());
        // 分页
        int fromIndex = (pageNo - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, answeredQuestionDTOS.size());
        List<AnsweredQuestionDTO> pagedList = fromIndex < answeredQuestionDTOS.size() ? answeredQuestionDTOS.subList(fromIndex, toIndex) : java.util.Collections.emptyList();
        // 组装StudentAnswerDTO
        StudentAnswerDTO studentAnswerDTO = new StudentAnswerDTO();
        studentAnswerDTO.setUserDTO(userDTO);
        studentAnswerDTO.setAnsweredQuestionDTOS(pagedList);
        return studentAnswerDTO;
    }

    private String camelToSnake(String str) {
        Pattern pattern = Pattern.compile("[A-Z]");
        Matcher matcher = pattern.matcher(str);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(result);
        return result.toString();
    }

    public boolean isFieldExists(Class<?> clazz, String fieldName) {
        try {
            clazz.getDeclaredField(fieldName);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    private AnswerHistoryDO convertToEntity(AnswerHistoryDTO dto) {
        if (dto == null) return null;
        AnswerHistoryDO entity = new AnswerHistoryDO();
        entity.setId(dto.getId());
        entity.setUserId(dto.getUserId());
        entity.setQuestionId(dto.getQuestionId());
        entity.setUserAnswer(dto.getUserAnswer());
        entity.setIsCorrect(dto.getIsCorrect());
        entity.setAnswerTime(dto.getAnswerTime());
        return entity;
    }

    private AnswerHistoryDTO convertToDTO(AnswerHistoryDO entity) {
        if (entity == null) return null;
        AnswerHistoryDTO dto = new AnswerHistoryDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setQuestionId(entity.getQuestionId());
        dto.setUserAnswer(entity.getUserAnswer());
        dto.setIsCorrect(entity.getIsCorrect());
        dto.setAnswerTime(entity.getAnswerTime());
        return dto;
    }
} 