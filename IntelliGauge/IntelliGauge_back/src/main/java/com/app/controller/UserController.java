package com.app.controller;

import com.app.dto.UserDTO;
import com.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
@CrossOrigin
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 添加用户信息
     */
    @PostMapping("/add")
    public boolean addUser(@RequestBody UserDTO user) {
        System.out.println(user);
        return userService.addUser(user);
    }

    /**
     * 根据ID删除用户
     */
    @DeleteMapping("/delete/{id}")
    public boolean deleteUserById(@PathVariable Integer id) {
        return userService.deleteUserById(id);
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/delete/batch")
    public boolean deleteUsersByIds(@RequestBody List<Integer> ids) {
        return userService.deleteUsersByIds(ids);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public boolean updateUser(@RequestBody UserDTO user) {
        System.out.println("aaa");
        return userService.updateUser(user);
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/get/{id}")
    public UserDTO getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    /**
     * 查询所有用户
     */
    @GetMapping("/list")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * 分页查询用户
     */
    @PostMapping("/page")
    public Object getAllUsersByPage(@RequestParam(defaultValue = "1") Integer pageNo,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestBody Map<String, Object> conditions) {
        return userService.getAllUsersByPage(pageNo, pageSize, conditions);
    }
} 