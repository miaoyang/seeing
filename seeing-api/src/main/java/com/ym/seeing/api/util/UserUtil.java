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
    public static final String SYSNAME = "root,selectdomain,image,TOIMG," +
            "user,users,admin,retrievepass,deleteimg,360,components,log";
    /**
     * 获取用户信息
     * @return
     */
    public static User getUser(){
        Subject subject = SecurityUtils.getSubject();
        return (User) subject.getPrincipal();
    }

    /**
     * 是否具备管理员权限
     * @return
     */
    public static boolean hasAdmin(){
        Subject subject = SecurityUtils.getSubject();
        if (subject.hasRole("admin")){
            return true;
        }
        return false;
    }


    public static Boolean checkSysName(String name){
        boolean b = true;
        if(SYSNAME.contains(name)){
            b = false;
        }
        return b ;
    }
}
