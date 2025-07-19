package com.app.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.app.dao.LectureParticipantsDAO;
import com.app.dto.LectureParticipantsDTO;
import com.app.entity.LectureParticipantsDO;
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
public class LectureParticipantsService {
    @Resource
    private LectureParticipantsDAO lectureParticipantsDAO;

    public boolean addLectureParticipants(LectureParticipantsDTO lectureParticipants) {
        LectureParticipantsDO lectureParticipantsEntity = convertToEntity(lectureParticipants);
        return lectureParticipantsDAO.insert(lectureParticipantsEntity) > 0;
    }

    public boolean deleteLectureParticipantsById(Integer id) {
        return lectureParticipantsDAO.deleteById(id) > 0;
    }

    public boolean deleteLectureParticipantsByIds(List<Integer> ids) {
        return lectureParticipantsDAO.deleteBatchIds(ids) > 0;
    }

    public boolean updateLectureParticipants(LectureParticipantsDTO lectureParticipants) {
        LectureParticipantsDO lectureParticipantsEntity = convertToEntity(lectureParticipants);
        return lectureParticipantsDAO.updateById(lectureParticipantsEntity) > 0;
    }

    public LectureParticipantsDTO getLectureParticipantsById(Integer id) {
        LectureParticipantsDO lectureParticipantsEntity = lectureParticipantsDAO.selectById(id);
        return convertToDTO(lectureParticipantsEntity);
    }

    public List<LectureParticipantsDTO> getAllLectureParticipants() {
        List<LectureParticipantsDO> lectureParticipantsList = lectureParticipantsDAO.selectList(null);
        return lectureParticipantsList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<LectureParticipantsDTO> getAllLectureParticipantsByPage(Integer pageNo, Integer pageSize, Map<String, Object> conditions) {
        Page<LectureParticipantsDO> page = new Page<>(pageNo, pageSize);
        QueryWrapper<LectureParticipantsDO> queryWrapper = new QueryWrapper<>();

        conditions.forEach((key, value) -> {
            if (value != null && isFieldExists(LectureParticipantsDO.class, key)) {
                queryWrapper.like(camelToSnake(key), value);
            }
        });

        Page<LectureParticipantsDO> resultPage = lectureParticipantsDAO.selectPage(page, queryWrapper);
        List<LectureParticipantsDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<LectureParticipantsDTO> result = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
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

    private LectureParticipantsDO convertToEntity(LectureParticipantsDTO lectureParticipantsDTO) {
        if (lectureParticipantsDTO == null) {
            return null;
        }
        LectureParticipantsDO lectureParticipants = new LectureParticipantsDO();
        lectureParticipants.setId(lectureParticipantsDTO.getId());
        lectureParticipants.setLectureId(lectureParticipantsDTO.getLectureId());
        lectureParticipants.setUserId(lectureParticipantsDTO.getUserId());
        lectureParticipants.setRole(lectureParticipantsDTO.getRole());
        lectureParticipants.setAnonymous(lectureParticipantsDTO.getAnonymous());
        return lectureParticipants;
    }

    private LectureParticipantsDTO convertToDTO(LectureParticipantsDO lectureParticipants) {
        if (lectureParticipants == null) {
            return null;
        }
        LectureParticipantsDTO lectureParticipantsDTO = new LectureParticipantsDTO();
        lectureParticipantsDTO.setId(lectureParticipants.getId());
        lectureParticipantsDTO.setLectureId(lectureParticipants.getLectureId());
        lectureParticipantsDTO.setUserId(lectureParticipants.getUserId());
        lectureParticipantsDTO.setRole(lectureParticipants.getRole());
        lectureParticipantsDTO.setAnonymous(lectureParticipants.getAnonymous());
        return lectureParticipantsDTO;
    }
} 