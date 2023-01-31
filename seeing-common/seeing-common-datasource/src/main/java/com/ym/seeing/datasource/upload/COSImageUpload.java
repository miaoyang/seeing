package com.ym.seeing.datasource.upload;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.ListObjectsRequest;
import com.qcloud.cos.model.ObjectListing;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import com.ym.seeing.datasource.domain.Images;
import com.ym.seeing.datasource.domain.Keys;
import com.ym.seeing.datasource.domain.ReturnImage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/13 15:04
 * @Desc:
 */
@Service
@Slf4j
public class COSImageUpload extends AbstractImageUpload{
    static COSClient cosClient;
    static Keys key;

    @Override
    public ReturnImage uploadImage(
            Map<Map<String, String>, File> fileMap, String username, Integer keyID) {
        ReturnImage returnImage = new ReturnImage();
        File file = null;
        try {
            for (Map.Entry<Map<String, String>, File> entry : fileMap.entrySet()) {
                String prefix = entry.getKey().get("prefix");
                String shortUidName = entry.getKey().get("name");
                file = entry.getValue();
                try {
                    String bucketName = key.getBucketName();
                    String userKey = username + "/" + shortUidName + "." + prefix;
                    PutObjectRequest putObjectRequest =
                            new PutObjectRequest(bucketName, userKey, file);
                    PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
                } catch (CosServiceException serverException) {
                    returnImage.setCode("400");
                    serverException.printStackTrace();
                } catch (CosClientException clientException) {
                    returnImage.setCode("400");
                    clientException.printStackTrace();
                }
            }
            returnImage.setCode("200");
        } catch (Exception e) {
            e.printStackTrace();
            returnImage.setCode("500");
        }
        return returnImage;
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
        String secretId = k.getAccessKey();
        String secretKey = k.getAccessSecret();
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        Region region = new Region(k.getEndpoint());
        ClientConfig clientConfig = new ClientConfig(region);
        COSClient cos = new COSClient(cred, clientConfig);
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
        listObjectsRequest.setBucketName(k.getBucketName());
        listObjectsRequest.setDelimiter("/");
        listObjectsRequest.setMaxKeys(1);
        ObjectListing objectListing = null;
        try {
            objectListing = cos.listObjects(listObjectsRequest);
            ret = 1;
            cosClient = cos;
            key = k;
        } catch (Exception e) {
            log.error("COS Object Is null");
            ret = -1;
        }
        return ret;
    }
    @Override
    public Boolean deleteById(Integer keyID, Images images) {
        boolean b = true;
        try {
            cosClient.deleteObject(key.getBucketName(), images.getImgName());
        } catch (Exception e) {
            e.printStackTrace();
            b = false;
        }
        return b;
    }

}
