package com.humble.usercenter.service;

import com.humble.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* 用户服务
* @author hongbohu
* @description 针对表【user】的数据库操作Service
* @createDate 2023-11-23 21:05:17
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount user account
     * @param userPassword user password
     * @param checkPassword retype password
     * @return new user id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     * @param userAccount user account
     * @param userPassword user password
     * @return 返回脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest httpServletRequest);

    /**
     * 用户脱敏
     * @param originUser origin user
     * @return safe user
     */
    User getSafeUser(User originUser);
}
