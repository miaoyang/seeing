package com.ym.seeing.datasource.upload;

import com.UpYun;
import com.aliyun.oss.model.ObjectMetadata;
import com.ym.seeing.core.domain.Msg;
import com.ym.seeing.core.utils.TypeDictUtil;
import com.ym.seeing.datasource.domain.Images;
import com.ym.seeing.datasource.domain.Keys;
import com.ym.seeing.datasource.domain.ReturnImage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/13 16:23
 * @Desc:
 */
@Service
@Slf4j
public class USSImageUpload extends AbstractImageUpload{
    static UpYun upyun;
    static Keys key;

    @Override
    public ReturnImage uploadImage(Map<Map<String, String>, File> fileMap, String username, Integer keyID) {
        ReturnImage returnImage = new ReturnImage();
        File file = null;
        ObjectMetadata meta = new ObjectMetadata();
        meta.setHeader("Content-Disposition", "inline");
        try {
            for (Map.Entry<Map<String, String>, File> entry : fileMap.entrySet()) {
                String prefix = entry.getKey().get("prefix");
                String shortUidName = entry.getKey().get("name");
                file = entry.getValue();
                Msg fileMiME = TypeDictUtil.fileMiME(file);
                meta.setHeader("content-type", fileMiME.getData().toString());
                upyun.setContentMD5(UpYun.md5(file));
                boolean result =
                        upyun.writeFile(username + "/" + shortUidName + "." + prefix, file, true);
                if (result) {

                } else {
                    System.err.println("上传失败");
                    returnImage.setCode("400");
                }
            }
            returnImage.setCode("200");
        } catch (Exception e) {
            e.printStackTrace();
            returnImage.setCode("500");
        }
        return returnImage;
    }

    @Override
    public Boolean deleteById(Integer keyID, Images images) {
        boolean b = true;
        try {
            boolean result = upyun.deleteFile(images.getImgName(), null);
        } catch (Exception e) {
            e.printStackTrace();
            b = false;
        }
        return b;
    }

    public static Integer Initialize(Keys k) {
        int ret = -1;
        if (StringUtils.isBlank(k.getAccessKey())
                || StringUtils.isBlank(k.getKeyName())
                || StringUtils.isBlank(k.getAccessSecret())
                || StringUtils.isBlank(k.getEndpoint())
                || StringUtils.isBlank(k.getBucketName())
                || StringUtils.isBlank(k.getRequestAddress())
                || k.getStorageType() == null) {
            return -1;
        }
        UpYun upObj = new UpYun(k.getBucketName(), k.getAccessKey(), k.getAccessSecret());
        List<UpYun.FolderItem> items = null;
        try {
            items = upObj.readDir("/", null);
            ret = 1;
            upyun = upObj;
            key = k;
        } catch (Exception e) {
            System.out.println("USS Object Is null");
            ret = -1;
        }
        return ret;
    }
}
