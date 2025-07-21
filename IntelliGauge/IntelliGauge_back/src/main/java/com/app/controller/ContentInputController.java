package com.app.controller;

import com.app.dto.ContentInputDTO;
import com.app.service.ContentInputService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/content-input")
@Slf4j
@CrossOrigin
public class ContentInputController {
    @Resource
    private ContentInputService contentInputService;

    /**
     * 添加内容输入信息
     */
    @PostMapping("/add")
    public boolean addContentInput(@RequestBody ContentInputDTO contentInput) {
        return contentInputService.addContentInput(contentInput);
    }

    /**
     * 根据ID删除内容输入
     */
    @DeleteMapping("/delete/{id}")
    public boolean deleteContentInputById(@PathVariable Integer id) {
        return contentInputService.deleteContentInputById(id);
    }

    /**
     * 批量删除内容输入
     */
    @DeleteMapping("/delete/batch")
    public boolean deleteContentInputsByIds(@RequestBody List<Integer> ids) {
        return contentInputService.deleteContentInputsByIds(ids);
    }

    /**
     * 更新内容输入信息
     */
    @PutMapping("/update")
    public boolean updateContentInput(@RequestBody ContentInputDTO contentInput) {
        return contentInputService.updateContentInput(contentInput);
    }

    /**
     * 根据ID查询内容输入
     */
    @GetMapping("/get/{id}")
    public ContentInputDTO getContentInputById(@PathVariable Integer id) {
        return contentInputService.getContentInputById(id);
    }

    /**
     * 查询所有内容输入
     */
    @GetMapping("/list")
    public List<ContentInputDTO> getAllContentInputs() {
        return contentInputService.getAllContentInputs();
    }

    /**
     * 分页查询内容输入
     */
    @PostMapping("/page")
    public Object getAllContentInputsByPage(@RequestParam(defaultValue = "1") Integer pageNo,
                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                           @RequestBody Map<String, Object> conditions) {
        return contentInputService.getAllContentInputsByPage(pageNo, pageSize, conditions);
    }
} 