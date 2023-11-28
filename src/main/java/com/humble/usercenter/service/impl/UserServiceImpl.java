package com.humble.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.humble.usercenter.model.domain.User;
import com.humble.usercenter.service.UserService;
import com.humble.usercenter.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.humble.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author hongbohu
* 用户服务实现类
* @description 针对表【user】的数据库操作Service实现
* @createDate 2023-11-23 21:05:17
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "humble";


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return -1;
        }
        if (userAccount.length() < 4) {
            return -1;
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            return -1;
        }
        //账户不包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()) {
            return -1;
        }
        //密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            return -1;
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            return -1;
        }
        // 2.加密
        String encryptPwd = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        System.out.println(encryptPwd);
        // 3.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPwd);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }
        //账户不包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()) {
            return null;
        }
        // 2.加密
        String encryptPwd = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        System.out.println("encryptPwd: " + encryptPwd);
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPwd);
        User user = userMapper.selectOne(queryWrapper); // 调用dao方法根据queryWrapper查询用户 返回用户
        // 可能是账户密码输错/用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword or user does not exist");
            return null;
        }
        // 这里可补充的点：限流 一个ip登录次数过多 进行限流

        // 3. 用户脱敏
        User safeUser = getSafeUser(user);
        // 4. 登录成功 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safeUser); //.setAttribute(key, value)
        return safeUser;
    }

    /**
     * 用户脱敏
     * @param originUser 查询出来的user
     * @return 封装后去除密码的用户信息
     */
    @Override
    public User getSafeUser(User originUser) {
        User safeUser = new User();
        // generate all setter with default value
        safeUser.setId(originUser.getId());
        safeUser.setUsername(originUser.getUsername());
        safeUser.setUserAccount(originUser.getUserAccount());
        safeUser.setAvatarUrl(originUser.getAvatarUrl());
        safeUser.setGender(originUser.getGender());
        safeUser.setPhone(originUser.getPhone());
        safeUser.setEmail(originUser.getEmail());
        safeUser.setUserRole(originUser.getUserRole());
        safeUser.setUserStatus(originUser.getUserStatus());
        safeUser.setCreateTime(originUser.getCreateTime());
        return safeUser;
    }
}




