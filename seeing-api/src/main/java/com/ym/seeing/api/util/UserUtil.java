package com.ym.seeing.api.util;

import com.ym.seeing.core.domain.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/12 19:12
 * @Desc:
 */
public class UserUtil {
    /**
     * 获取用户信息
     * @return
     */
    public static User getUser(){
        Subject subject = SecurityUtils.getSubject();
        return (User) subject.getPrincipal();
    }
}
