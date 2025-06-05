package com.ht.bnu_tiku_backend.config;

import com.ht.bnu_tiku_backend.utils.WordUtils;
import com.ht.bnu_tiku_backend.utils.interceptor.LoginInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Resource
    LoginInterceptor loginInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 匹配所有请求
                .allowedOriginPatterns("*") // 允许的前端源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true); // 允许携带 cookie
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
            .addPathPatterns("/**") // ❗需要登录的接口路径.excludePathPatterns("/login")
            .excludePathPatterns(
                    "/user/login",
                    "/user/register",
                    "/question/**",
                    "/chat/**",
                    "/kp/**",
                    "/v3/**",
                    "/swagger-ui/**");
    }
}
