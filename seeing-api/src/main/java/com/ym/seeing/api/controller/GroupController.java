package com.ym.seeing.api.controller;

import com.ym.seeing.api.domain.Group;
import com.ym.seeing.api.service.IGroupService;
import com.ym.seeing.core.domain.Msg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/16 17:08
 * @Desc: 分组控制
 */
@Controller
@Slf4j
public class GroupController {

    @Autowired
    private IGroupService groupService;

    @PostMapping("/getGrouplistForUsers")
    @ResponseBody
    public Msg getGrouplistForUsers() {
        Msg msg = new Msg();
        List<Group> groupList = groupService.groupList(0);
        msg.setData(groupList);
        return msg;
    }


}
