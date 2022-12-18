package com.ym.seeing.auth.config;

import com.ym.seeing.core.domain.User;
import com.ym.seeing.rpc.service.IRemoteUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/20 21:57
 * @Desc: 用户的授权和认证
 */
@Component
@Qualifier("userRealm")
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private IRemoteUserService remoteUserService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        ArrayList<String> roleList = new ArrayList<>();
        // 添加管理员和普通用户两种角色
        if (user.getLevel() == 2){
            roleList.add("admin");
            roleList.add("user");
        }else {
            roleList.add("user");
        }
        // TODO 添加权限

        simpleAuthorizationInfo.addRoles(roleList);
        return simpleAuthorizationInfo;
    }

    /**
     * 认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
        User user = new User();
        user.setEmail(usernamePasswordToken.getUsername());
        User u = remoteUserService.getUser(user);
        if (u==null){
            return null;
        }
        return new SimpleAuthenticationInfo(u,u.getPassword(),"");
    }
}
