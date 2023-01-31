package com.ym.seeing.datasource.upload;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.ym.seeing.core.domain.Msg;
import com.ym.seeing.core.utils.TypeDictUtil;
import com.ym.seeing.datasource.domain.Images;
import com.ym.seeing.datasource.domain.Keys;
import com.ym.seeing.datasource.domain.ReturnImage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/13 16:17
 * @Desc:
 */
@Service
public class OSSImageUpload extends AbstractImageUpload{
    static OSS ossClient;
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
                PutObjectRequest putObjectRequest =
                        new PutObjectRequest(
                                key.getBucketName(),
                                username + "/" + shortUidName + "." + prefix,
                                file,
                                meta);
                ossClient.putObject(putObjectRequest);
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
            ossClient.deleteObject(key.getBucketName(), images.getImgName());
        } catch (Exception e) {
            e.printStackTrace();
            b = false;
        }
        return b;
    }

    public static Integer initialize(Keys k) {
        int ret = -1;
        ObjectListing objectListing = null;
        if (StringUtils.isBlank(k.getAccessKey())
                || StringUtils.isBlank(k.getAccessSecret())
                || StringUtils.isBlank(k.getEndpoint())
                || StringUtils.isBlank(k.getBucketName())
                || StringUtils.isBlank(k.getRequestAddress())) {
            return -1;
        }
        OSS ossObj =
                new OSSClientBuilder()
                        .build(k.getEndpoint(), k.getAccessKey(), k.getAccessSecret());
        try {
            objectListing = ossObj.listObjects(k.getBucketName());
            ret = 1;
            ossClient = ossObj;
            key = k;
        } catch (Exception e) {
            System.out.println("OSS Object Is null");
            ret = -1;
        }

        return ret;
    }
}
