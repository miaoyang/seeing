package com.ym.seeing.api.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ym.seeing.api.domain.ImgData;
import com.ym.seeing.api.mapper.CodeMapper;
import com.ym.seeing.api.mapper.UserMapper;
import com.ym.seeing.api.service.ICodeService;
import com.ym.seeing.api.service.IUserService;
import com.ym.seeing.core.domain.User;
import com.ym.seeing.core.exception.GlobalException;
import com.ym.seeing.core.utils.PrintUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private ICodeService codeService;

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
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getToken,token);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public User getUsers(User user) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (user.getId() != null){
            lambdaQueryWrapper.eq(User::getId,user.getId());
        }
        if (user.getEmail() != null){
            lambdaQueryWrapper.eq(User::getEmail,user.getEmail());
        }
        if (user.getUid() != null){
            lambdaQueryWrapper.eq(User::getUid,user.getUid());
        }
        if (user.getToken() != null){
            lambdaQueryWrapper.eq(User::getToken,user.getToken());
        }
        return userMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public Integer insertImg(ImgData img) {
        return null;
    }

    @Override
    public Integer change(User user) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getUid,user.getUid());
        if (StrUtil.isNotEmpty(user.getEmail())){
            wrapper.set(User::getEmail,user.getEmail());
        }
        if (StrUtil.isNotEmpty(user.getUserName())){
            wrapper.set(User::getUserName,user.getUserName());
        }
        if (StrUtil.isNotEmpty(user.getPassword())){
            wrapper.set(User::getPassword,user.getPassword());
        }
        if (StrUtil.isNotEmpty(user.getMemory())){
            wrapper.set(User::getMemory,user.getMemory());
        }
        if (user.getGroupId() != null){
            wrapper.set(User::getGroupId,user.getGroupId());
        }
        if (StrUtil.isNotEmpty(user.getToken())){
            wrapper.set(User::getToken,user.getToken());
        }
        return userMapper.update(user,wrapper);
    }

    @Override
    public Integer changeUser(User user) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getUid,user.getUid());
        if (StrUtil.isNotEmpty(user.getEmail())){
            wrapper.set(User::getEmail,user.getEmail());
        }
        if (StrUtil.isNotEmpty(user.getUserName())){
            wrapper.set(User::getUserName,user.getUserName());
        }
        if (StrUtil.isNotEmpty(user.getPassword())){
            wrapper.set(User::getPassword,user.getPassword());
        }
        if (StrUtil.isNotEmpty(user.getMemory())){
            wrapper.set(User::getMemory,user.getMemory());
        }
        if (user.getGroupId() != null){
            wrapper.set(User::getGroupId,user.getGroupId());
        }
        if (StrUtil.isNotEmpty(user.getToken())){
            wrapper.set(User::getToken,user.getToken());
        }
        if (user.getIsOk() != null){
            wrapper.set(User::getIsOk,user.getIsOk());
        }
        return userMapper.update(user,wrapper);
    }

    @Override
    public Integer getUserTotal() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        return userMapper.selectCount(wrapper);
    }

    @Override
    public List<User> getUserList(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(User::getUserName,username).or().like(User::getEmail,username);
        wrapper.orderByDesc(User::getBirthder);
        return userMapper.selectList(wrapper);
    }

    @Override
    public Integer delUser(Integer id) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId,id);
        return userMapper.delete(wrapper);
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
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getUid,uid);
        wrapper.set(User::getIsOk,1);
        return userMapper.update(null,wrapper);
    }

    @Override
    public User getUsersMail(String uid) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUid,uid);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public Integer setIsOk(User user) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId,user.getId());
        wrapper.set(User::getIsOk,user.getIsOk());
        return userMapper.update(user,wrapper);
    }

    @Override
    public Integer setMemory(User user) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId,user.getId());
        wrapper.set(User::getMemory,user.getMemory());
        return userMapper.update(user,wrapper);
    }

    @Override
    public User getUsersById(Integer id) {
        return userMapper.selectById(id);
    }

    @Override
    public List<User> getUserListByGroupId(Integer groupid) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getGroupId,groupid);
        return userMapper.selectList(wrapper);
    }

    @Override
    public void updateUserUid(String uid) {
        LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(User::getUid,uid);
        lambdaUpdateWrapper.set(User::getIsOk,1);
        userMapper.update(null, lambdaUpdateWrapper);
    }


    @Override
    @Transactional
    public Integer userSetMemory(User newMemoryUser, String code) {
        Integer ret = this.changeUser(newMemoryUser);
        if(ret<=0){
            PrintUtil.warning("用户空间没有设置成功。回滚");
            throw new GlobalException("用户没有设置成功。");
        }else{
            ret = codeService.deleteCode(code);
        }
        return ret;
    }

    @Override
    public List<User> getRecentlyUser() {
        return userMapper.getRecentlyUser();
    }
}
