package com.ym.seeing.api.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/18 18:45
 * @Desc:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "upload_config")
@ToString
public class UploadConfig {
    /**
     * 用户主键
     */
    private Integer id;
    /**
     * 支持后缀
     */
    private String suffix;
    /**
     * 游客文件大小
     */
    @TableField(value = "file_size_tourists")
    private String fileSizeTourists;
    /**
     * 用户文件大小
     */
    @TableField(value = "file_size_user")
    private String fileSizeUser;
    /**
     * 游客文件总数量，超过则不允许加入队列
     */
    @TableField(value = "img_count_tourists")
    private Integer imgCountTourists;
    /**
     * 用户文件总数量，超过则不允许加入队列
     */
    @TableField(value = "img_count_user")
    private Integer imgCountUser;
    /**
     * url链接类型
     */
    @TableField(value = "url_type")
    private Integer urlType;
    /**
     * 禁止游客上传
     */
    @TableField(value = "is_update")
    private Integer isUpdate;
    /**
     * 开启api
     */
    private Integer api;
    /**
     * 游客内存
     */
    @TableField(value = "visitor_memory")
    private String visitorMemory;
    /**
     * 用户内存
     */
    @TableField(value = "user_memory")
    private String userMemory;
    /**
     * 黑名单列表
     */
    @TableField(value = "black_list")
    private String blackList;
    /**
     * 用户上传开关
     */
    @TableField(value = "user_close")
    private Integer userClose;
}
