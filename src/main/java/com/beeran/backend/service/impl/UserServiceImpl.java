package com.beeran.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beeran.backend.model.domain.User;
import com.beeran.backend.service.UserService;
import com.beeran.backend.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 用户服务实现类
 *
 * @author BeerAn
 */
@Service
@MapperScan("com.beeran.backend.model.domain.User")
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    private static final String SALT = "beeran";
    private static final String USER_LOGIN_STATUS  = "user_login";

    @Resource
    private UserMapper userMapper;

    @Override
    public long userRegister(String userAccount, String password, String checkPass) {
        //1.校验
        if (StringUtils.isAnyBlank(userAccount,password,checkPass)){
            System.out.println("账号，密码或校验密码为空");
            return  -1;
        }
        if (userAccount.length() < 4){
            System.out.println("账号长度最小为4位");
            return -1;
        }
        if (!password.equals(checkPass)){
            System.out.println("两次密码输入不一致");
            return -1;
        }
        if (password.length() < 6){
            System.out.println("密码长度最低为6");
            return -1;
        }

        // 账号不能包含特殊字符
        String pattern = "\\pP|\\pS|\\s";
        Matcher matcher = Pattern.compile(pattern).matcher(userAccount);
        if (matcher.find()){
            System.out.println("不能使用特殊字符");
            return -1;
        }

        // 账号不能重复
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("_account", userAccount);
        long count = this.count(query);
        if (count > 0){
            System.out.println("已存在相同账号");
            return -1;
        }
        // 2.加密

        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());

        // 3.插入数据
        User user = new User();
        user.set_account(userAccount);
        user.set_password(encryptPassword);
        boolean save = this.save(user);
        if (!save){
            System.out.println("插入数据失败");

            return -1;
        }
        return user.getId();
    }


    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest req) {
        //1.校验
        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            System.out.println("账号，密码或校验密码为空");
            return  null;
        }
        if (userAccount.length() < 4){
            System.out.println("账号长度最小为4位");
            return null;
        }

        if (userPassword.length() < 6){
            System.out.println("密码长度最低为6");
            return null;
        }

        // 账号不能包含特殊字符
        String pattern = "\\pP|\\pS|\\s";
        Matcher matcher = Pattern.compile(pattern).matcher(userAccount);
        if (matcher.find()){
            System.out.println("不能使用特殊字符");
            return null;
        }
        // 2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("_account",userAccount);
        queryWrapper.eq("_password",encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null){
            return null;
        }
        // 3.用户脱敏
        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUsername(user.getUsername());
        safeUser.set_account(user.get_account());
        safeUser.setAvatar_url(user.getAvatar_url());
        safeUser.setGender(user.getGender());
        safeUser.setPhone(user.getPhone());
        safeUser.setEmail(user.getEmail());
        safeUser.set_status(user.get_status());
        safeUser.setCreate_time(user.getCreate_time());

        // 4.记录用户登录状态
        req.getSession().setAttribute(USER_LOGIN_STATUS, safeUser);
        return safeUser;

    }
}




