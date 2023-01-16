package com.beeran.backend.service;

import com.beeran.backend.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * userService
 */
public interface UserService extends IService<User> {
    /**
     * 用户注册
     * @param userCount 用户账号
     * @param password 密码
     * @param checkPass 校验密码
     * @return 新用户ID
     */
    long userRegister(String userAccount, String password, String checkPass);

    /**
     * 用户登录
     * @param userAccount 用户账号
     * @param userPassword 密码
     * @return 查询用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest req);

}
