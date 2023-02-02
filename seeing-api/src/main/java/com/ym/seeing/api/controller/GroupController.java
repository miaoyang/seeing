package com.ym.seeing.api.controller;

import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ym.seeing.api.domain.Group;
import com.ym.seeing.api.service.IGroupService;
import com.ym.seeing.core.constant.GlobalConstant;
import com.ym.seeing.core.domain.Msg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/16 17:08
 * @Desc: 分组控制
 */
@Controller
@RequestMapping("/admin/root")
@Slf4j
public class GroupController {

    @Autowired
    private IGroupService groupService;

    @PostMapping("/getGrouplistForUsers")
    @ResponseBody
    public Msg getGroupListForUsers() {
        Msg msg = new Msg();
        List<Group> groupList = groupService.groupList(0);
        msg.setData(groupList);
        return msg;
    }

    @PostMapping("/getGroupList")
    @ResponseBody
    public Msg getGroupList(@RequestParam(value = "data", defaultValue = "") String data) {
        Msg msg = new Msg();
        JSONObject jsonObject = JSONObject.parseObject(data);
        Integer pageNum = jsonObject.getInteger("pageNum");
        Integer pageSize = jsonObject.getInteger("pageSize");
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = GlobalConstant.PAGE_SIZE;
        }
        PageHelper.startPage(pageNum, pageSize);
        try {
            List<Group> groupList = groupService.groupList(0);
            PageInfo<Group> pageInfo = new PageInfo<>(groupList);
            msg.setData(pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            msg.setInfo("服务器内部错误！");
            msg.setCode("500");
        }
        return msg;
    }

    @PostMapping(value = "/addGroup")
    @ResponseBody
    public Msg addGroup(@RequestParam(value = "data", defaultValue = "") String data) {
        JSONObject jsonObject = JSONObject.parseObject(data);
        Group group = new Group();
        group.setGroupName(jsonObject.getString("groupname"));
        group.setKeyId(jsonObject.getInteger("keyid"));
        group.setUserType(jsonObject.getInteger("usertype"));
        group.setCompress(jsonObject.getBoolean("compress") ? 1 : 0);
        Msg msg = groupService.addGroup(group);
        return msg;
    }

    @PostMapping("/updateGroup")
    @ResponseBody
    public Msg updateGroup(@RequestParam(value = "data", defaultValue = "") String data) {
        JSONObject jsonObject = JSONObject.parseObject(data);
        Group group = new Group();
        group.setId(jsonObject.getInteger("id"));
        if (jsonObject.getInteger("id") == 1) {
            group.setGroupName("默认群组");
            group.setUserType(0);
        } else {
            group.setGroupName(jsonObject.getString("groupname"));
            group.setUserType(jsonObject.getInteger("usertype"));
        }
        group.setKeyId(jsonObject.getInteger("keyid"));
        group.setCompress(jsonObject.getBoolean("compress") ? 1 : 0);
        Msg msg = groupService.updateGroup(group);
        return msg;
    }

    @PostMapping(value = "/deleGroup")
    @ResponseBody
    public Msg deleteGroup(@RequestParam(value = "data",defaultValue = "")String data){
        Msg msg = new Msg();
        JSONObject jsonObject = JSONObject.parseObject(data);
        Integer id = jsonObject.getInteger("id");
        if (id != 1){
            return groupService.deleteGroup(id);
        }else {
            msg.setCode("500");
            msg.setInfo("默认群组不可删除");
            return msg;
        }
    }

}
