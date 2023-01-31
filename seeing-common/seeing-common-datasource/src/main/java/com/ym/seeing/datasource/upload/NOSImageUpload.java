package com.ym.seeing.datasource.upload;

import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.ObjectListing;
import com.ym.seeing.datasource.domain.Images;
import com.ym.seeing.datasource.domain.Keys;
import com.ym.seeing.datasource.domain.ReturnImage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/13 16:13
 * @Desc:
 */
@Service
public class NOSImageUpload extends AbstractImageUpload{
    static NosClient nosClient;
    static Keys key;

    @Override
    public ReturnImage uploadImage(Map<Map<String, String>, File> fileMap, String username, Integer keyID) {
        ReturnImage returnImage = new ReturnImage();
        File file = null;
        try {
            for (Map.Entry<Map<String, String>, File> entry : fileMap.entrySet()) {
                String prefix = entry.getKey().get("prefix");
                String shortUidName = entry.getKey().get("name");
                file = entry.getValue();
                nosClient.putObject(
                        key.getBucketName(), username + "/" + shortUidName + "." + prefix, file);
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
        boolean b =true;
        try {
            nosClient.deleteObject(key.getBucketName(), images.getImgName());
        } catch (Exception e) {
            e.printStackTrace();
            b =false;
        }
        return b;
    }

    public static Integer initialize(Keys k) {
        int ret = -1;
        if (StringUtils.isBlank(k.getAccessKey())
                || StringUtils.isBlank(k.getAccessSecret())
                || StringUtils.isBlank(k.getEndpoint())
                || StringUtils.isBlank(k.getBucketName())
                || StringUtils.isBlank(k.getRequestAddress())) {
            return -1;
        }
        Credentials credentials = new BasicCredentials(k.getAccessKey(), k.getAccessSecret());
        NosClient nosObj = new NosClient(credentials);
        nosObj.setEndpoint(k.getEndpoint());
        ObjectListing objectListing = null;
        try {
            objectListing = nosObj.listObjects(k.getBucketName());
            ret = 1;
            nosClient = nosObj;
            key = k;
        }catch (Exception e){
            System.out.println("NOS Object Is null");
            ret = -1;
        }
        return ret;
    }
}
