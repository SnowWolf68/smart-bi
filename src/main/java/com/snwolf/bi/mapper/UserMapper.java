package com.snwolf.bi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.snwolf.bi.domain.entity.User;
import org.apache.ibatis.annotations.Update;

public interface UserMapper extends BaseMapper<User> {

    @Update("update user set left_cnt = left_cnt - 1 where id = #{userId}")
    void deduckCnt(Long userId);
}
