package com.snwolf.bi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.snwolf.bi.domain.dto.UserLoginDTO;
import com.snwolf.bi.domain.dto.UserRegisterDTO;
import com.snwolf.bi.domain.entity.User;
import com.snwolf.bi.exception.CheckPasswordException;

public interface IUserService extends IService<User> {
    void register(UserRegisterDTO userRegisterDTO) throws CheckPasswordException;

    String login(UserLoginDTO userLoginDTO);

    void deduckCnt(Long userId);
}
