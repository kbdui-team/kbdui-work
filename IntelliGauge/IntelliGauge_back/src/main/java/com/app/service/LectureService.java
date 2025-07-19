package com.app.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.app.dao.LectureDAO;
import com.app.dto.LectureDTO;
import com.app.entity.LectureDO;
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
public class LectureService {
    @Resource
    private LectureDAO lectureDAO;

    public boolean addLecture(LectureDTO lecture) {
        LectureDO lectureEntity = convertToEntity(lecture);
        return lectureDAO.insert(lectureEntity) > 0;
    }

    public boolean deleteLectureById(Integer id) {
        return lectureDAO.deleteById(id) > 0;
    }

    public boolean deleteLecturesByIds(List<Integer> ids) {
        return lectureDAO.deleteBatchIds(ids) > 0;
    }

    public boolean updateLecture(LectureDTO lecture) {
        LectureDO lectureEntity = convertToEntity(lecture);
        return lectureDAO.updateById(lectureEntity) > 0;
    }

    public LectureDTO getLectureById(Integer id) {
        LectureDO lectureEntity = lectureDAO.selectById(id);
        return convertToDTO(lectureEntity);
    }

    public List<LectureDTO> getAllLectures() {
        List<LectureDO> lectureList = lectureDAO.selectList(null);
        return lectureList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<LectureDTO> getAllLecturesByPage(Integer pageNo, Integer pageSize, Map<String, Object> conditions) {
        Page<LectureDO> page = new Page<>(pageNo, pageSize);
        QueryWrapper<LectureDO> queryWrapper = new QueryWrapper<>();

        conditions.forEach((key, value) -> {
            if (value != null && isFieldExists(LectureDO.class, key)) {
                queryWrapper.like(camelToSnake(key), value);
            }
        });

        Page<LectureDO> resultPage = lectureDAO.selectPage(page, queryWrapper);
        List<LectureDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<LectureDTO> result = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
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

    private LectureDO convertToEntity(LectureDTO lectureDTO) {
        if (lectureDTO == null) {
            return null;
        }
        LectureDO lecture = new LectureDO();
        lecture.setId(lectureDTO.getId());
        lecture.setTitle(lectureDTO.getTitle());
        lecture.setDescription(lectureDTO.getDescription());
        lecture.setTeacherId(lectureDTO.getTeacherId());
        lecture.setStatus(lectureDTO.getStatus());
        return lecture;
    }

    private LectureDTO convertToDTO(LectureDO lecture) {
        if (lecture == null) {
            return null;
        }
        LectureDTO lectureDTO = new LectureDTO();
        lectureDTO.setId(lecture.getId());
        lectureDTO.setTitle(lecture.getTitle());
        lectureDTO.setDescription(lecture.getDescription());
        lectureDTO.setTeacherId(lecture.getTeacherId());
        lectureDTO.setStatus(lecture.getStatus());
        return lectureDTO;
    }
} 