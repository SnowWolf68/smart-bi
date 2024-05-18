package com.snwolf.bi.controller;

import cn.hutool.core.bean.BeanUtil;
import com.snwolf.bi.domain.dto.UserDTO;
import com.snwolf.bi.domain.dto.UserLoginDTO;
import com.snwolf.bi.domain.dto.UserRegisterDTO;
import com.snwolf.bi.domain.entity.User;
import com.snwolf.bi.result.Result;
import com.snwolf.bi.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping("/register")
    public Result register(@RequestBody UserRegisterDTO userRegisterDTO){
        userService.register(userRegisterDTO);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody UserLoginDTO userLoginDTO){
        String token = userService.login(userLoginDTO);
        return Result.success(token);
    }

    @GetMapping("/list")
    public Result<List<UserDTO>> list(){
        List<User> userList = userService.list();
        List<UserDTO> userDTOList = userList.stream().map(user -> BeanUtil.copyProperties(user, UserDTO.class)).collect(Collectors.toList());
        return Result.success(userDTOList);
    }
}
