package com.app.controller;

import com.app.dto.LectureDTO;
import com.app.service.LectureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lecture")
@Slf4j
public class LectureController {
    @Resource
    private LectureService lectureService;

    /**
     * 添加讲座信息
     */
    @PostMapping("/add")
    public boolean addLecture(@RequestBody LectureDTO lecture) {
        return lectureService.addLecture(lecture);
    }

    /**
     * 根据ID删除讲座
     */
    @DeleteMapping("/delete/{id}")
    public boolean deleteLectureById(@PathVariable Integer id) {
        return lectureService.deleteLectureById(id);
    }

    /**
     * 批量删除讲座
     */
    @DeleteMapping("/delete/batch")
    public boolean deleteLecturesByIds(@RequestBody List<Integer> ids) {
        return lectureService.deleteLecturesByIds(ids);
    }

    /**
     * 更新讲座信息
     */
    @PutMapping("/update")
    public boolean updateLecture(@RequestBody LectureDTO lecture) {
        return lectureService.updateLecture(lecture);
    }

    /**
     * 根据ID查询讲座
     */
    @GetMapping("/get/{id}")
    public LectureDTO getLectureById(@PathVariable Integer id) {
        return lectureService.getLectureById(id);
    }

    /**
     * 查询所有讲座
     */
    @GetMapping("/list")
    public List<LectureDTO> getAllLectures() {
        return lectureService.getAllLectures();
    }

    /**
     * 分页查询讲座
     */
    @PostMapping("/page")
    public Object getAllLecturesByPage(@RequestParam(defaultValue = "1") Integer pageNo,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestBody Map<String, Object> conditions) {
        return lectureService.getAllLecturesByPage(pageNo, pageSize, conditions);
    }
} 