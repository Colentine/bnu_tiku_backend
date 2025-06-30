package com.ht.bnu_tiku_backend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.bnu_tiku_backend.mapper.UserMapper;
import com.ht.bnu_tiku_backend.model.domain.User;
import com.ht.bnu_tiku_backend.service.UserService;
import com.ht.bnu_tiku_backend.utils.DTO.LoginResultDTO;
import com.ht.bnu_tiku_backend.utils.DTO.UserDTO;
import com.ht.bnu_tiku_backend.utils.ResponseResult.Result;
import com.ht.bnu_tiku_backend.utils.UserHolder;
import com.ht.bnu_tiku_backend.utils.redisservice.RedisObjectService;
import com.ht.bnu_tiku_backend.utils.request.UserLoginRequest;
import com.ht.bnu_tiku_backend.utils.request.UserRegisterRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


/**
 * @author huangtao
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2025-05-13 13:58:00
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private RedisObjectService redisObjectService;

    @Resource
    private UserMapper userMapper;

    @Override
    public Result<LoginResultDTO> login(UserLoginRequest param) {
        // 1. 参数校验（用 StrUtil 替代 if-null 判断）
        if (StrUtil.hasBlank(param.getUserName(), param.getUserPassword())) {
            throw new IllegalArgumentException("名称或密码不能为空");
        }

        if (param.getUserPassword().length() < 4) {
            throw new IllegalArgumentException("密码过短");
        }

        if (param.getUserPassword().length() > 100) {
            throw new IllegalArgumentException("密码过长");
        }

        if (param.getUserName().length() > 100) {
            throw new IllegalArgumentException("账号过长");
        }

        if (param.getUserName().length() < 2) {
            throw new IllegalArgumentException("账号过短");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", param.getUserName());
        // 2. 查询数据库中的用户（防 SQL 注入 + 简洁写法）
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new RuntimeException("用户名不存在");
        }

        // 3. 密码比对（使用 Hutool 的 MD5）
        String inputPwd = SecureUtil.md5(param.getUserPassword());
        if (!inputPwd.equals(user.getUserPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 4. 生成唯一 token（可替换为 JWT）
        String token = IdUtil.simpleUUID(); // 32位UUID，无“-”

        // 5. 脱敏后构建 DTO（Hutool BeanUtil 超简洁）
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);

        UserHolder.setUser(userDTO);
        log.info(UserHolder.getUser().toString());
        // 6. 存入 Redis（30分钟有效期）
        String redisKey = "login:token:" + token;
        redisObjectService.set(redisKey, userDTO, 30, TimeUnit.DAYS);

        // 7. 构造返回结果
        LoginResultDTO resloginResultDTO = new LoginResultDTO();
        resloginResultDTO.setToken(token);
        resloginResultDTO.setUserInfo(userDTO);

        return Result.ok(resloginResultDTO);
    }

    @Override
    public Result<UserDTO> register(UserRegisterRequest req) {
        // 1. 密码一致性校验
        if (!StrUtil.equals(req.getUserPassword(), req.getCheckPassword())) {
            throw new IllegalArgumentException("两次密码不一致");
        }

        // 2. 邮箱格式校验（Hutool）
        if (!Validator.isEmail(req.getEmail())) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", req.getEmail());
        User user = userMapper.selectOne(queryWrapper);
        // 3. 是否已存在
        if (user != null) {
            throw new IllegalArgumentException("该邮箱已注册");
        }

        // 4. 密码加密（Spring Security）
        String inputPwd = SecureUtil.md5(req.getUserPassword());

        // 5. 构建用户对象
        req.setUserPassword(inputPwd);

        UserDTO userDTO = BeanUtil.copyProperties(req, UserDTO.class);

        String userAccount = String.valueOf(userMapper.selectCount(null) + 1L);

        if (userAccount.length() < 6) {
            userAccount = StrUtil.padPre(userAccount, 6, "0");
        }

        userDTO.setUserRole("0");

        userDTO.setUserAccount(userAccount);

        User registerUser = BeanUtil.copyProperties(req, User.class);

        registerUser.setUserAccount(userAccount);

        userMapper.insert(registerUser);

        return Result.ok(userDTO);
    }
}




