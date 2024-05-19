package com.snwolf.bi.controller;

import cn.hutool.core.bean.BeanUtil;
import com.snwolf.bi.domain.dto.UserDTO;
import com.snwolf.bi.domain.dto.UserLoginDTO;
import com.snwolf.bi.domain.dto.UserRegisterDTO;
import com.snwolf.bi.domain.entity.User;
import com.snwolf.bi.result.Result;
import com.snwolf.bi.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Api(tags = "用户相关接口")
public class UserController {

    private final IUserService userService;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result register(@RequestBody UserRegisterDTO userRegisterDTO){
        userService.register(userRegisterDTO);
        return Result.success();
    }

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<String> login(@RequestBody UserLoginDTO userLoginDTO){
        String token = userService.login(userLoginDTO);
        return Result.success(token);
    }

    @GetMapping("/list")
    @ApiOperation("查询用户列表")
    public Result<List<UserDTO>> list(){
        List<User> userList = userService.list();
        List<UserDTO> userDTOList = userList.stream().map(user -> BeanUtil.copyProperties(user, UserDTO.class)).collect(Collectors.toList());
        return Result.success(userDTOList);
    }
}
