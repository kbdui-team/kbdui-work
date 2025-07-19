package com.app.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.app.dao.QuestionOptionsDAO;
import com.app.dto.QuestionOptionsDTO;
import com.app.entity.QuestionOptionsDO;
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
public class QuestionOptionsService {
    @Resource
    private QuestionOptionsDAO questionOptionsDAO;

    public boolean addQuestionOptions(QuestionOptionsDTO questionOptions) {
        QuestionOptionsDO questionOptionsEntity = convertToEntity(questionOptions);
        return questionOptionsDAO.insert(questionOptionsEntity) > 0;
    }

    public boolean deleteQuestionOptionsById(Integer id) {
        return questionOptionsDAO.deleteById(id) > 0;
    }

    public boolean deleteQuestionOptionsByIds(List<Integer> ids) {
        return questionOptionsDAO.deleteBatchIds(ids) > 0;
    }

    public boolean updateQuestionOptions(QuestionOptionsDTO questionOptions) {
        QuestionOptionsDO questionOptionsEntity = convertToEntity(questionOptions);
        return questionOptionsDAO.updateById(questionOptionsEntity) > 0;
    }

    public QuestionOptionsDTO getQuestionOptionsById(Integer id) {
        QuestionOptionsDO questionOptionsEntity = questionOptionsDAO.selectById(id);
        return convertToDTO(questionOptionsEntity);
    }

    public List<QuestionOptionsDTO> getAllQuestionOptions() {
        List<QuestionOptionsDO> questionOptionsList = questionOptionsDAO.selectList(null);
        return questionOptionsList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<QuestionOptionsDTO> getAllQuestionOptionsByPage(Integer pageNo, Integer pageSize, Map<String, Object> conditions) {
        Page<QuestionOptionsDO> page = new Page<>(pageNo, pageSize);
        QueryWrapper<QuestionOptionsDO> queryWrapper = new QueryWrapper<>();

        conditions.forEach((key, value) -> {
            if (value != null && isFieldExists(QuestionOptionsDO.class, key)) {
                queryWrapper.like(camelToSnake(key), value);
            }
        });

        Page<QuestionOptionsDO> resultPage = questionOptionsDAO.selectPage(page, queryWrapper);
        List<QuestionOptionsDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<QuestionOptionsDTO> result = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
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

    private QuestionOptionsDO convertToEntity(QuestionOptionsDTO questionOptionsDTO) {
        if (questionOptionsDTO == null) {
            return null;
        }
        QuestionOptionsDO questionOptions = new QuestionOptionsDO();
        questionOptions.setId(questionOptionsDTO.getId());
        questionOptions.setQuestionId(questionOptionsDTO.getQuestionId());
        questionOptions.setOptionText(questionOptionsDTO.getOptionText());
        questionOptions.setOptionOrder(questionOptionsDTO.getOptionOrder());
        questionOptions.setIsCorrect(questionOptionsDTO.getIsCorrect());
        return questionOptions;
    }

    private QuestionOptionsDTO convertToDTO(QuestionOptionsDO questionOptions) {
        if (questionOptions == null) {
            return null;
        }
        QuestionOptionsDTO questionOptionsDTO = new QuestionOptionsDTO();
        questionOptionsDTO.setId(questionOptions.getId());
        questionOptionsDTO.setQuestionId(questionOptions.getQuestionId());
        questionOptionsDTO.setOptionText(questionOptions.getOptionText());
        questionOptionsDTO.setOptionOrder(questionOptions.getOptionOrder());
        questionOptionsDTO.setIsCorrect(questionOptions.getIsCorrect());
        return questionOptionsDTO;
    }
} 