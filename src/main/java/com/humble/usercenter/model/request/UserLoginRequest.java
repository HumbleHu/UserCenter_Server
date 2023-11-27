package com.humble.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author hongbohu
 */
@Data //lombok注解 自动生成getter和setter
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = -2900723961955153848L;

    private String userAccount;
    private String userPassword;
}
