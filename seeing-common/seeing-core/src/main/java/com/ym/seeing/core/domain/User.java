package com.ym.seeing.core.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/21 14:48
 * @Desc:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "user")
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 用户名称
     */
    @TableField(value = "user_name")
    private String userName;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 用户邮箱
     */
    private String email;
    /**
     * 注册时间
     */
    private String birthder;
    /**
     * 用户级别
     */
    private Integer level;
    /**
     * 唯一标识符
     */
    private String uid;
    /**
     *
     */
    @TableField(value = "is_ok")
    private Integer isOk;
    /**
     * 内存
     */
    private  String memory;
    /**
     * 分组id
     */
    @TableField(value = "group_id")
    private Integer groupId;
    /**
     * 分组名称
     */
    @TableField(value = "group_name")
    private String groupName;
    /**
     * 访问token
     */
    private String token;
}
