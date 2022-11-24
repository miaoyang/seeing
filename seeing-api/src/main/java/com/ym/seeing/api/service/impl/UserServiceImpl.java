package com.ym.seeing.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ym.seeing.api.domain.ImgData;
import com.ym.seeing.api.mapper.UserMapper;
import com.ym.seeing.api.service.IUserService;
import com.ym.seeing.core.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/18 19:39
 * @Desc:
 */
@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public Integer register(User user) {
        return userMapper.insert(user);
    }

    @Override
    public Integer login(String email, String password, String uid) {
        return null;
    }

    @Override
    public User loginByToken(String token) {
        return null;
    }

    @Override
    public User getUsers(User user) {
        return null;
    }

    @Override
    public Integer insertImg(ImgData img) {
        return null;
    }

    @Override
    public Integer change(User user) {
        return null;
    }

    @Override
    public Integer changeUser(User user) {
        return null;
    }

    @Override
    public Integer checkUsername(String username) {
        return null;
    }

    @Override
    public Integer getUserTotal() {
        return null;
    }

    @Override
    public List<User> getUserList(String username) {
        return null;
    }

    @Override
    public Integer delUser(Integer id) {
        return null;
    }

    @Override
    public Integer countUsername(String username) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUserName,username);
        return userMapper.selectCount(lambdaQueryWrapper);
    }

    @Override
    public Integer countMail(String email) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getEmail,email);
        return userMapper.selectCount(lambdaQueryWrapper);
    }

    @Override
    public Integer uidUser(String uid) {
        return null;
    }

    @Override
    public User getUsersMail(String uid) {
        return null;
    }

    @Override
    public Integer setIsOk(User user) {
        return null;
    }

    @Override
    public Integer setMemory(User user) {
        return null;
    }

    @Override
    public User getUsersById(Integer id) {
        return null;
    }

    @Override
    public List<User> getUserListByGroupId(Integer groupid) {
        return null;
    }
}
