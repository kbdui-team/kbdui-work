package com.app.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.app.dao.UserPreferencesDAO;
import com.app.dto.UserPreferencesDTO;
import com.app.entity.UserPreferencesDO;
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
public class UserPreferencesService {
    @Resource
    private UserPreferencesDAO userPreferencesDAO;

    public boolean addUserPreferences(UserPreferencesDTO userPreferences) {
        UserPreferencesDO userPreferencesEntity = convertToEntity(userPreferences);
        return userPreferencesDAO.insert(userPreferencesEntity) > 0;
    }

    public boolean deleteUserPreferencesById(Integer id) {
        return userPreferencesDAO.deleteById(id) > 0;
    }

    public boolean deleteUserPreferencesByIds(List<Integer> ids) {
        return userPreferencesDAO.deleteBatchIds(ids) > 0;
    }

    public boolean updateUserPreferences(UserPreferencesDTO userPreferences) {
        UserPreferencesDO userPreferencesEntity = convertToEntity(userPreferences);
        return userPreferencesDAO.updateById(userPreferencesEntity) > 0;
    }

    public UserPreferencesDTO getUserPreferencesById(Integer id) {
        UserPreferencesDO userPreferencesEntity = userPreferencesDAO.selectById(id);
        return convertToDTO(userPreferencesEntity);
    }

    public List<UserPreferencesDTO> getAllUserPreferences() {
        List<UserPreferencesDO> userPreferencesList = userPreferencesDAO.selectList(null);
        return userPreferencesList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<UserPreferencesDTO> getAllUserPreferencesByPage(Integer pageNo, Integer pageSize, Map<String, Object> conditions) {
        Page<UserPreferencesDO> page = new Page<>(pageNo, pageSize);
        QueryWrapper<UserPreferencesDO> queryWrapper = new QueryWrapper<>();

        conditions.forEach((key, value) -> {
            if (value != null && isFieldExists(UserPreferencesDO.class, key)) {
                queryWrapper.like(camelToSnake(key), value);
            }
        });

        Page<UserPreferencesDO> resultPage = userPreferencesDAO.selectPage(page, queryWrapper);
        List<UserPreferencesDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<UserPreferencesDTO> result = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
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

    private UserPreferencesDO convertToEntity(UserPreferencesDTO userPreferencesDTO) {
        if (userPreferencesDTO == null) {
            return null;
        }
        UserPreferencesDO userPreferences = new UserPreferencesDO();
        userPreferences.setId(userPreferencesDTO.getId());
        userPreferences.setUserId(userPreferencesDTO.getUserId());
        userPreferences.setAllowAnonymous(userPreferencesDTO.getAllowAnonymous());
        return userPreferences;
    }

    private UserPreferencesDTO convertToDTO(UserPreferencesDO userPreferences) {
        if (userPreferences == null) {
            return null;
        }
        UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO();
        userPreferencesDTO.setId(userPreferences.getId());
        userPreferencesDTO.setUserId(userPreferences.getUserId());
        userPreferencesDTO.setAllowAnonymous(userPreferences.getAllowAnonymous());
        return userPreferencesDTO;
    }
} 