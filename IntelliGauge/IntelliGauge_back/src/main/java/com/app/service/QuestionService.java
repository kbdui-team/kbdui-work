package com.app.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.app.dao.QuestionDAO;
import com.app.dto.QuestionDTO;
import com.app.entity.QuestionDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QuestionService {
    @Resource
    private QuestionDAO questionDAO;

    public boolean addQuestion(QuestionDTO question) {
        QuestionDO questionEntity = convertToEntity(question);
        return questionDAO.insert(questionEntity) > 0;
    }

    public boolean deleteQuestionById(Integer id) {
        return questionDAO.deleteById(id) > 0;
    }

    public boolean deleteQuestionsByIds(List<Integer> ids) {
        return questionDAO.deleteBatchIds(ids) > 0;
    }

    public boolean updateQuestion(QuestionDTO question) {
        QuestionDO questionEntity = convertToEntity(question);
        return questionDAO.updateById(questionEntity) > 0;
    }

    public QuestionDTO getQuestionById(Integer id) {
        QuestionDO questionEntity = questionDAO.selectById(id);
        return convertToDTO(questionEntity);
    }

    public List<QuestionDTO> getAllQuestions() {
        List<QuestionDO> questionList = questionDAO.selectList(null);
        return questionList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<QuestionDTO> getAllQuestionsByPage(Integer pageNo, Integer pageSize, Map<String, Object> conditions) {
        Page<QuestionDO> page = new Page<>(pageNo, pageSize);
        QueryWrapper<QuestionDO> queryWrapper = new QueryWrapper<>();

        conditions.forEach((key, value) -> {
            if (value != null && isFieldExists(QuestionDO.class, key)) {
                queryWrapper.like(camelToSnake(key), value);
            }
        });

        Page<QuestionDO> resultPage = questionDAO.selectPage(page, queryWrapper);
        List<QuestionDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<QuestionDTO> result = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        result.setRecords(records);
        return result;
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

    private QuestionDO convertToEntity(QuestionDTO questionDTO) {
        if (questionDTO == null) {
            return null;
        }
        QuestionDO question = new QuestionDO();
        question.setId(questionDTO.getId());
        question.setLectureId(questionDTO.getLectureId());
        question.setContentInputId(questionDTO.getContentInputId());
        question.setQuestionType(questionDTO.getQuestionType());
        question.setQuestionText(questionDTO.getQuestionText());
        return question;
    }

    private QuestionDTO convertToDTO(QuestionDO question) {
        if (question == null) {
            return null;
        }
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setId(question.getId());
        questionDTO.setLectureId(question.getLectureId());
        questionDTO.setContentInputId(question.getContentInputId());
        questionDTO.setQuestionType(question.getQuestionType());
        questionDTO.setQuestionText(question.getQuestionText());
        return questionDTO;
    }
} 