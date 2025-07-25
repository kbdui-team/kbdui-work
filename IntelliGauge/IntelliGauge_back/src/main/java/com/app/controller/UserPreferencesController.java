package com.app.controller;

import com.app.dto.UserPreferencesDTO;
import com.app.service.UserPreferencesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user-preferences")
@Slf4j
@CrossOrigin
public class UserPreferencesController {
    @Resource
    private UserPreferencesService userPreferencesService;

    /**
     * 添加用户偏好设置
     */
    @PostMapping("/add")
    public boolean addUserPreferences(@RequestBody UserPreferencesDTO userPreferences) {
        return userPreferencesService.addUserPreferences(userPreferences);
    }

    /**
     * 根据ID删除用户偏好设置
     */
    @DeleteMapping("/delete/{id}")
    public boolean deleteUserPreferencesById(@PathVariable Integer id) {
        return userPreferencesService.deleteUserPreferencesById(id);
    }

    /**
     * 批量删除用户偏好设置
     */
    @DeleteMapping("/delete/batch")
    public boolean deleteUserPreferencesByIds(@RequestBody List<Integer> ids) {
        return userPreferencesService.deleteUserPreferencesByIds(ids);
    }

    /**
     * 更新用户偏好设置
     */
    @PutMapping("/update")
    public boolean updateUserPreferences(@RequestBody UserPreferencesDTO userPreferences) {
        return userPreferencesService.updateUserPreferences(userPreferences);
    }

    /**
     * 根据ID查询用户偏好设置
     */
    @GetMapping("/get/{id}")
    public UserPreferencesDTO getUserPreferencesById(@PathVariable Integer id) {
        return userPreferencesService.getUserPreferencesById(id);
    }

    /**
     * 查询所有用户偏好设置
     */
    @GetMapping("/list")
    public List<UserPreferencesDTO> getAllUserPreferences() {
        return userPreferencesService.getAllUserPreferences();
    }

    /**
     * 分页查询用户偏好设置
     */
    @PostMapping("/page")
    public Object getAllUserPreferencesByPage(@RequestParam(defaultValue = "1") Integer pageNo,
                                             @RequestParam(defaultValue = "10") Integer pageSize,
                                             @RequestBody Map<String, Object> conditions) {
        return userPreferencesService.getAllUserPreferencesByPage(pageNo, pageSize, conditions);
    }
} 