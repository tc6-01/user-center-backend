package com.beeran.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.beeran.backend.model.domain.User;
import com.beeran.backend.model.domain.request.UserLoginRequest;
import com.beeran.backend.model.domain.request.UserRegisterRequest;
import com.beeran.backend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.beeran.backend.constant.UserConstant.ADMIN_ROLE;
import static com.beeran.backend.constant.UserConstant.USER_LOGIN_STATUS;


/**
 * 用户接口
 *
 * @Author BeerAn
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    // 用户注册
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
    // 用户登录
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
    // 查询用户
    @GetMapping("/search")
    public List<User> searchUsers(String userName, HttpServletRequest request){
        // 鉴权，仅管理员可搜索
        if (isAdmin(request)){
            return new ArrayList<>();
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(userName)){
            userQueryWrapper.like("username",userName);
        }
        List<User> userList = userService.list(userQueryWrapper);
        return userList.stream().map(user -> {
            user.set_password(null);
            return user;
        }).collect(Collectors.toList());
    }

    @PostMapping("/delete")
    public boolean deleteUsers(@RequestBody long id, HttpServletRequest request) {

        if (id <= 0) {
            return false;
        }
        if (isAdmin(request)) {
            return false;
        }
        return userService.removeById(id);
    }



    private boolean isAdmin(@RequestBody HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATUS);
        User user = (User) userObj;
        if (user == null || user.getRole() != ADMIN_ROLE){
            return  false;
        }
        return true;
    }

}
