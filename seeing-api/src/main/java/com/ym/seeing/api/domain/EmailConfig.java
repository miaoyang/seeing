package com.ym.seeing.api.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/18 18:41
 * @Desc:
 */
@Data
@AllArgsConstructor
@TableName(value = "email_config")
public class EmailConfig {
    /**
     * 主键id
     */
    private Integer id ;
    /**
     * 邮箱
     */
    private String emails;
    /**
     * 授权码
     */
    @TableField(value = "email_key")
    private String emailKey;
    /**
     * 服务器
     */
    @TableField(value = "email_url")
    private String emailUrl;
    /**
     * 端口
     */
    private String port;
    /**
     * 用户名
     */
    @TableField(value = "email_name")
    private String emailName;
    /**
     * 1为可用，其他标识不可用
     */
    @TableField(value = "is_using")
    private Integer isUsing ;
}
