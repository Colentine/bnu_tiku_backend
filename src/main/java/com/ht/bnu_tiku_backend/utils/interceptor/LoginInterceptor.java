package com.ht.bnu_tiku_backend.utils.interceptor;

import com.ht.bnu_tiku_backend.utils.DTO.UserDTO;
import com.ht.bnu_tiku_backend.utils.Exception.NoLoginException;
import com.ht.bnu_tiku_backend.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    public LoginInterceptor() {
        System.out.println("🔥 LoginInterceptor 构造方法触发！");
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String method = request.getMethod();
        System.out.println("请求方法：" + method);

        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        // 校验登录态
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            throw new NoLoginException("未登录");
        }

        return true;
    }
}
