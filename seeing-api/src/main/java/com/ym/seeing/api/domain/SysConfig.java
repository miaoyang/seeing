package com.ym.seeing.api.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/18 18:57
 * @Desc:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_config")
public class SysConfig {
    /**
     * 主键ID
     */
    private Integer id;
    /**
     * 是否注册
     */
    private Integer register;
    /**
     * 检测雷同？
     */
    @TableField(value = "check_duplicate")
    private String checkDuplicate;
}
