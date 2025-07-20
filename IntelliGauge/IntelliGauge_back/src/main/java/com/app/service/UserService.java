package com.app.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.app.dao.UserDAO;
import com.app.dto.UserDTO;
import com.app.entity.UserDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@CrossOrigin
public class UserService {
    @Resource
    private UserDAO userDAO;

    public boolean addUser(UserDTO user) {
        // 将 UserDTO 转换为 UserDO 实体类
        UserDO userEntity = convertToEntity(user);
        // 使用 userDAO 插入数据
        return userDAO.insert(userEntity) > 0;
    }

    public boolean deleteUserById(Integer id) {
        // 使用 userDAO 根据 ID 删除
        return userDAO.deleteById(id) > 0;
    }

    public boolean deleteUsersByIds(List<Integer> ids) {
        // 批量删除
        return userDAO.deleteBatchIds(ids) > 0;
    }

    public boolean updateUser(UserDTO user) {
        // 将 UserDTO 转换为 UserDO 实体类
        UserDO userEntity = convertToEntity(user);
        System.out.println("1:");
        System.out.println(user);
        // 使用 userDAO 更新数据
        return userDAO.updateById(userEntity) > 0;
    }

    public UserDTO getUserById(Integer id) {
        // 根据 ID 查询
        UserDO userEntity = userDAO.selectById(id);
        // 将查询结果转换为 UserDTO
        return convertToDTO(userEntity);
    }

    public List<UserDTO> getAllUsers() {
        // 查询所有用户
        List<UserDO> userList = userDAO.selectList(null);
        // 将每个 UserDO 转换为 UserDTO
        return userList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<UserDTO> getAllUsersByPage(Integer pageNo, Integer pageSize, Map<String, Object> conditions) {
        // 创建分页对象
        Page<UserDO> page = new Page<>(pageNo, pageSize);

        // 创建 QueryWrapper 构建查询条件
        QueryWrapper<UserDO> queryWrapper = new QueryWrapper<>();

        // 动态添加查询条件
        conditions.forEach((key, value) -> {
            if (value != null && isFieldExists(UserDO.class, key)) {
                queryWrapper.like(camelToSnake(key), value);
            }
        });

        // 使用 MyBatis-Plus 的分页查询
        Page<UserDO> resultPage = userDAO.selectPage(page, queryWrapper);

        // 将每个 UserDO 转换为 UserDTO
        List<UserDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<UserDTO> result = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        result.setRecords(records);

        return result;
    }

    // 驼峰转下划线的工具方法
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

    // Java的反射机制
    public boolean isFieldExists(Class<?> clazz, String fieldName) {
        try {
            clazz.getDeclaredField(fieldName);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    // 将 UserDTO 转换为 UserDO 实体类
    private UserDO convertToEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        UserDO user = new UserDO();
        user.setId(userDTO.getId());
        user.setUserName(userDTO.getUserName());
        user.setRealName(userDTO.getRealName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setStatus(userDTO.getStatus());
        user.setPassword(userDTO.getPassword());
        user.setUserType(userDTO.getUserType());
        return user;
    }

    // 将 UserDO 实体类转换为 UserDTO
    private UserDTO convertToDTO(UserDO user) {
        if (user == null) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUserName(user.getUserName());
        userDTO.setRealName(user.getRealName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        userDTO.setStatus(user.getStatus());
        userDTO.setPassword(user.getPassword());
        userDTO.setUserType(user.getUserType());
        return userDTO;
    }
} 