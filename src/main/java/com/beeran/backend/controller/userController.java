package com.beeran.backend.controller;

import com.beeran.backend.model.domain.User;
import com.beeran.backend.model.domain.request.UserLoginRequest;
import com.beeran.backend.model.domain.request.UserRegisterRequest;
import com.beeran.backend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户接口
 *
 * @Author BeerAn
 */
@RestController
@RequestMapping("/user")
public class userController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return null;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String password = userRegisterRequest.getPassword();
        String checkPass = userRegisterRequest.getCheckPass();
        // controller 提前校验，不涉及业务逻辑
        if (StringUtils.isAnyBlank(userAccount, password, checkPass)) {
            return null;
        }

        long id = userService.userRegister(userAccount, password, checkPass);
        return id;
    }
    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest req) {
        if (userLoginRequest == null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String password = userLoginRequest.getPassword();
        // controller 提前校验，不涉及业务逻辑
        if (StringUtils.isAnyBlank(userAccount, password)) {
            return null;
        }

        return userService.userLogin(userAccount, password, req);
    }
}
