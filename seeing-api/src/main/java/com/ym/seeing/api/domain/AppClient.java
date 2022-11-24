package com.ym.seeing.api.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/24 14:41
 * @Desc:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "app_client")
public class AppClient {
    private String id;

    @TableField("is_use")
    private String isUse;

    @TableField("win_pakc_url")
    private String winPackUrl;

    @TableField("mac_pack_url")
    private String macPackUrl;

    @TableField("app_name")
    private String appName;

    @TableField("app_logo")
    private String appLogo;

    @TableField("app_update")
    private String appUpdate;
}
