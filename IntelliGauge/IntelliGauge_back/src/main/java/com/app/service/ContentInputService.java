package com.app.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.app.dao.ContentInputDAO;
import com.app.dto.ContentInputDTO;
import com.app.entity.ContentInputDO;
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
public class ContentInputService {
    @Resource
    private ContentInputDAO contentInputDAO;

    public boolean addContentInput(ContentInputDTO contentInput) {
        ContentInputDO contentInputEntity = convertToEntity(contentInput);
        return contentInputDAO.insert(contentInputEntity) > 0;
    }

    public boolean deleteContentInputById(Integer id) {
        return contentInputDAO.deleteById(id) > 0;
    }

    public boolean deleteContentInputsByIds(List<Integer> ids) {
        return contentInputDAO.deleteBatchIds(ids) > 0;
    }

    public boolean updateContentInput(ContentInputDTO contentInput) {
        ContentInputDO contentInputEntity = convertToEntity(contentInput);
        return contentInputDAO.updateById(contentInputEntity) > 0;
    }

    public ContentInputDTO getContentInputById(Integer id) {
        ContentInputDO contentInputEntity = contentInputDAO.selectById(id);
        return convertToDTO(contentInputEntity);
    }

    public List<ContentInputDTO> getAllContentInputs() {
        List<ContentInputDO> contentInputList = contentInputDAO.selectList(null);
        return contentInputList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<ContentInputDTO> getAllContentInputsByPage(Integer pageNo, Integer pageSize, Map<String, Object> conditions) {
        Page<ContentInputDO> page = new Page<>(pageNo, pageSize);
        QueryWrapper<ContentInputDO> queryWrapper = new QueryWrapper<>();

        conditions.forEach((key, value) -> {
            if (value != null && isFieldExists(ContentInputDO.class, key)) {
                queryWrapper.like(camelToSnake(key), value);
            }
        });

        Page<ContentInputDO> resultPage = contentInputDAO.selectPage(page, queryWrapper);
        List<ContentInputDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<ContentInputDTO> result = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
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

    private ContentInputDO convertToEntity(ContentInputDTO contentInputDTO) {
        if (contentInputDTO == null) {
            return null;
        }
        ContentInputDO contentInput = new ContentInputDO();
        contentInput.setId(contentInputDTO.getId());
        contentInput.setLectureId(contentInputDTO.getLectureId());
        contentInput.setContentType(contentInputDTO.getContentType());
        contentInput.setFileName(contentInputDTO.getFileName());
        contentInput.setFilePath(contentInputDTO.getFilePath());
        contentInput.setFileSize(contentInputDTO.getFileSize());
        return contentInput;
    }

    private ContentInputDTO convertToDTO(ContentInputDO contentInput) {
        if (contentInput == null) {
            return null;
        }
        ContentInputDTO contentInputDTO = new ContentInputDTO();
        contentInputDTO.setId(contentInput.getId());
        contentInputDTO.setLectureId(contentInput.getLectureId());
        contentInputDTO.setContentType(contentInput.getContentType());
        contentInputDTO.setFileName(contentInput.getFileName());
        contentInputDTO.setFilePath(contentInput.getFilePath());
        contentInputDTO.setFileSize(contentInput.getFileSize());
        return contentInputDTO;
    }
} 