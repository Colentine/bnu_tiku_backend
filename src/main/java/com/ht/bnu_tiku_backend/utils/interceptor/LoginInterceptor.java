package com.ht.bnu_tiku_backend.utils.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.ht.bnu_tiku_backend.model.domain.User;
import com.ht.bnu_tiku_backend.utils.DTO.UserDTO;
import com.ht.bnu_tiku_backend.utils.Exception.NoLoginException;
import com.ht.bnu_tiku_backend.utils.UserHolder;
import com.ht.bnu_tiku_backend.utils.redisservice.RedisObjectService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Resource
    private RedisObjectService redisObjectService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. 取出 Authorization 头
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return false;
        }

        String authHeader = request.getHeader("Authorization");
        if (StrUtil.isBlank(authHeader) || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        String token = authHeader.substring(7); // 去掉 "Bearer "
        String redisKey = "login:token:" + token;

        // 2. 从 Redis 取用户信息
        UserDTO userDTO = redisObjectService.get(redisKey, UserDTO.class);
        if (userDTO == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        // 4. 保存到 ThreadLocal
        UserHolder.setUser(userDTO);
        return true;
    }
}
