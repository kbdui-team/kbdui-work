package com.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.app.entity.UserPreferencesDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserPreferencesDAO extends BaseMapper<UserPreferencesDO> {
} 