package com.app.controller;

import com.app.dto.LectureParticipantsDTO;
import com.app.service.LectureParticipantsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lecture-participants")
@CrossOrigin
@Slf4j
public class LectureParticipantsController {
    @Resource
    private LectureParticipantsService lectureParticipantsService;

    /**
     * 添加讲座参与者信息
     */
    @PostMapping("/add")
    public boolean addLectureParticipants(@RequestBody LectureParticipantsDTO lectureParticipants) {
        return lectureParticipantsService.addLectureParticipants(lectureParticipants);
    }

    /**
     * 根据ID删除讲座参与者
     */
    @DeleteMapping("/delete/{id}")
    public boolean deleteLectureParticipantsById(@PathVariable Integer id) {
        return lectureParticipantsService.deleteLectureParticipantsById(id);
    }

    /**
     * 批量删除讲座参与者
     */
    @DeleteMapping("/delete/batch")
    public boolean deleteLectureParticipantsByIds(@RequestBody List<Integer> ids) {
        return lectureParticipantsService.deleteLectureParticipantsByIds(ids);
    }

    /**
     * 更新讲座参与者信息
     */
    @PutMapping("/update")
    public boolean updateLectureParticipants(@RequestBody LectureParticipantsDTO lectureParticipants) {
        return lectureParticipantsService.updateLectureParticipants(lectureParticipants);
    }

    /**
     * 根据ID查询讲座参与者
     */
    @GetMapping("/get/{id}")
    public LectureParticipantsDTO getLectureParticipantsById(@PathVariable Integer id) {
        return lectureParticipantsService.getLectureParticipantsById(id);
    }

    /**
     * 查询所有讲座参与者
     */
    @GetMapping("/list")
    public List<LectureParticipantsDTO> getAllLectureParticipants() {
        return lectureParticipantsService.getAllLectureParticipants();
    }

    /**
     * 分页查询讲座参与者
     */
    @PostMapping("/page")
    public Object getAllLectureParticipantsByPage(@RequestParam(defaultValue = "1") Integer pageNo,
                                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                                 @RequestBody Map<String, Object> conditions) {
        return lectureParticipantsService.getAllLectureParticipantsByPage(pageNo, pageSize, conditions);
    }
} 