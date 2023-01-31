package com.ym.seeing.api.service.impl;

import com.ym.seeing.api.domain.Group;
import com.ym.seeing.api.service.IGroupService;
import com.ym.seeing.api.service.IUserGroupService;
import com.ym.seeing.api.service.IUserService;
import com.ym.seeing.core.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/16 14:40
 * @Desc:
 */
@Component
@Slf4j
public class SourceServiceImpl {
    @Autowired
    private IGroupService groupService;
    @Autowired
    private IUserGroupService userGroupService;
    @Autowired
    private IUserService userService;

    private static IGroupService groupServiceImpl;
    private static IUserGroupService userGroupServiceImpl;
    private static IUserService userServiceImpl;

    @PostConstruct
    public void init() {
        groupServiceImpl =groupService;
        userGroupServiceImpl = userGroupService;
        userServiceImpl = userService;
    }

    public static Group GetSource(Integer userid) {
        //UserType 0-未分配 1-游客 2-用户 3-管理员
        User user =null;
        if(userid!=null){
            User u = new User();
            u.setId(userid);
            user = userServiceImpl.getUsers(u);
        }
        Group group =null;
        if(user==null){
            //游客
            Integer count = groupServiceImpl.getCountByUserType(1);
            if(count>0){
                group = groupServiceImpl.selectByUserType(1);
            }else{
                group = groupServiceImpl.selectById(1);
            }
        }else{
            //用户
            if(user.getGroupId()!=1){
                //说明自定义过的优先
                group = groupServiceImpl.selectById(user.getGroupId());
            }else{
                //默认的，用的是group主键为1的  但是还需要看看用户组有没有设置，比如管理员 用户
                if(user.getLevel()>1){
                    //先查询管理员用户组有没有 如果有就用 没有就默认
                    Integer count = groupServiceImpl.getCountByUserType(3);
                    if(count>0){
                        group = groupServiceImpl.selectByUserType(3);
                    }else{
                        group = groupServiceImpl.selectById(1);
                    }
                }else{
                    //先查询普通用户组有没有 如果有就用 没有就默认
                    Integer count = groupServiceImpl.getCountByUserType(2);
                    if(count>0){
                        group = groupServiceImpl.selectByUserType(2);

                    }else{
                        group = groupServiceImpl.selectById(1);
                    }
                }
            }
        }
        return group;
    }
}

