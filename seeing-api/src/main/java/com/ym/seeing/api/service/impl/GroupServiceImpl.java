package com.ym.seeing.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ym.seeing.api.domain.Group;
import com.ym.seeing.api.mapper.GroupMapper;
import com.ym.seeing.api.service.IGroupService;
import com.ym.seeing.api.service.IUserService;
import com.ym.seeing.core.domain.Msg;
import com.ym.seeing.core.domain.User;
import com.ym.seeing.core.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/16 14:43
 * @Desc:
 */
@Service
@Slf4j
public class GroupServiceImpl implements IGroupService {
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private IUserService userService;

    @Override
    public Integer getCountByUserType(int i) {
        LambdaQueryWrapper<Group> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Group::getUserType, i);
        return groupMapper.selectCount(lambdaQueryWrapper);
    }

    @Override
    public Group selectByUserType(int i) {
        LambdaQueryWrapper<Group> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Group::getUserType, i);
        return groupMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public Group selectById(int i) {
        return groupMapper.selectById(i);
    }

    @Override
    public List<Group> groupList(int i) {
        return groupMapper.groupList(i);
    }

    @Override
    public Msg addGroup(Group group) {
        final Msg msg = new Msg();
        if (group.getUserType() != 0) {
            Integer countByUserType = groupMapper.getCountByUserType(group.getUserType());
            if (countByUserType == 0) {
                groupMapper.addGroup(group);
                msg.setInfo("添加成功");
            } else {
                msg.setCode("110401");
                msg.setInfo("分配的该用户组已存在。请勿重复分配。");
            }
        } else {
            groupMapper.addGroup(group);
            msg.setInfo("添加成功");
        }
        return msg;
    }

    @Override
    public Msg updateGroup(Group group) {
        Msg msg = new Msg();
        if (group.getStorageType() != 0) {
            Group selectGroup = this.selectByUserType(group.getUserType());
            if (selectGroup != null) {
                if (selectGroup.getUserType().equals(group.getUserType())) {
                    if (selectGroup.getId().equals(group.getId())) {
                        this.updateGroupById(group);
                        msg.setInfo("修改成功");
                    } else {
                        msg.setCode("110401");
                        msg.setInfo("分配的该用户组已存在。请勿重复分配。");
                    }
                } else {
                    if (this.getCountByUserType(group.getUserType()) > 0) {
                        msg.setCode("110401");
                        msg.setInfo("分配的该用户组已存在。请勿重复分配。");
                    } else {
                        this.addGroup(group);
                        msg.setInfo("修改成功");
                    }
                }
            } else {
                this.updateGroupById(group);
                msg.setInfo("修改成功");
            }
        } else {
            this.updateGroupById(group);
            msg.setInfo("修改成功");
        }
        return msg;
    }

    @Override
    public Msg deleteGroup(Integer id) {
        Msg msg = new Msg();
        QueryWrapper<Group> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id);
        int deleteGroup = groupMapper.delete(wrapper);
        if (deleteGroup > 0) {
            List<User> userListByGroupId = userService.getUserListByGroupId(id);
            for (User user : userListByGroupId) {
                User tempUser = new User();
                tempUser.setGroupId(1);
                tempUser.setUid(user.getUid());
                userService.change(user);
            }
        } else {
            msg.setCode("500");
            msg.setInfo("删除成功");
            throw new GlobalException("用户之前没有设置成功！");
        }
        return null;
    }

    public void updateGroupById(Group group) {
        LambdaUpdateWrapper<Group> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Group::getId, group.getId());
        groupMapper.update(group, lambdaUpdateWrapper);
    }
}
