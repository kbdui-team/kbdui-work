package com.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.app.entity.UserDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDAO extends BaseMapper<UserDO> {
} 