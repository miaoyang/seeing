package com.ym.seeing.datasource.upload;

import com.ym.seeing.datasource.domain.Images;
import com.ym.seeing.datasource.domain.Keys;
import com.ym.seeing.datasource.domain.ReturnImage;

import java.io.File;
import java.util.Map;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/13 15:21
 * @Desc: 抽象上传图像
 */
public abstract class AbstractImageUpload {


    public abstract ReturnImage uploadImage(
            Map<Map<String, String>, File> fileMap, String username, Integer keyID);

    /**
     * 删除
     * @param keyID
     * @param images
     * @return
     */
    public abstract Boolean deleteById(Integer keyID, Images images);
}
