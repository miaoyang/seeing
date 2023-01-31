package com.ym.seeing.api.service;

import com.ym.seeing.api.domain.ImgData;
import com.ym.seeing.core.domain.User;

import java.util.List;

public interface IUserService {
    Integer register(User user);

    Integer login(String email, String password,String uid);

    User loginByToken (String token);

    User getUsers(User user);

    Integer insertImg(ImgData img);

    Integer change(User user);

    Integer changeUser(User user);

    Integer checkUsername(String username);

    Integer getUserTotal();

    List<User> getUserList(String username);

    Integer delUser(Integer id);

    Integer countUsername(String username);

    Integer countMail(String email);

    Integer uidUser(String uid);

    User getUsersMail(String uid);

    Integer setIsOk (User user);

    Integer setMemory(User user);

    User getUsersById(Integer id);

    List<User> getUserListByGroupId(Integer groupid);

    void updateUserUid(String uid);
}
