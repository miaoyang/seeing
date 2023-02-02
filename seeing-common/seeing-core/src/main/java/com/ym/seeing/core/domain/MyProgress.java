package com.ym.seeing.core.domain;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Yangmiao
 * @Date: 2023/2/2 19:02
 * @Desc:
 */
@Data
@ToString
public class MyProgress {
    /**
     * 已经成功的个数
     */
    private int delSuccessCount=0;
    /**
     * 已经失败的图片
     */
    private List<String> delErrorImgListt =  new ArrayList<>();
    /**
     * 控制开关  0代表false 1代表true
     */
    private int delOCT=0;
    /**
     * 删除成功的图片
     */
    private List<Integer> delSuccessImgList =  new ArrayList<>();

    public void initializeDelImg(){
        this.delSuccessCount=0;
        this.delErrorImgListt =  new ArrayList<>();
        this.delOCT=0;
        this.delSuccessImgList =  new ArrayList<>();
    }
}
