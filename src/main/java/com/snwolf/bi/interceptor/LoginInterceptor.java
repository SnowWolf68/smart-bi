package com.snwolf.bi.interceptor;

import com.snwolf.bi.domain.dto.UserDTO;
import com.snwolf.bi.utils.UserHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserDTO userDTO = UserHolder.getUser();
        if(userDTO == null){
            response.setStatus(401);
            return false;
        }
        return true;
    }

    /**
     * 移除用户, 避免内存泄漏
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
