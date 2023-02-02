package com.ym.seeing.datasource.upload;

import com.alibaba.fastjson.JSONObject;
import com.ym.seeing.core.domain.Msg;
import com.ym.seeing.core.domain.MyProgress;
import com.ym.seeing.core.exception.StorageSourceInitException;
import com.ym.seeing.datasource.constant.StorageConstant;
import com.ym.seeing.datasource.domain.Images;
import com.ym.seeing.datasource.domain.Keys;
import com.ym.seeing.datasource.domain.ReturnImage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/13 16:47
 * @Desc: 获取存储类型
 */
@Service
@Slf4j
public class GetSource {
    @Autowired
    private NOSImageUpload nosImageUpload;
    @Autowired
    private OSSImageUpload ossImageUpload;
    @Autowired
    private USSImageUpload ussImageUpload;
    @Autowired
    private KODOImageUpload kodoImageupload;
    @Autowired
    private COSImageUpload cosImageUpload;
    @Autowired
    private FtpImageUpload ftpImageUpload;
    @Autowired
    private UFileImageUpload uFileImageUpload;
    @Autowired
    private LocalImageUpload localImageUpload;

    public ReturnImage storageSource(Integer type, Map<Map<String, String>, File> fileMap, String userpath, Integer keyID) {
        ReturnImage returnImage = null;
        try {
            if (type.equals(StorageConstant.NOS)) {
                returnImage = nosImageUpload.uploadImage(fileMap, userpath, keyID);
            } else if (type.equals(StorageConstant.OSS)) {
                returnImage = ossImageUpload.uploadImage(fileMap, userpath, keyID);
            } else if (type.equals(StorageConstant.USS)) {
                returnImage = ussImageUpload.uploadImage(fileMap, userpath, keyID);
            } else if (type.equals(StorageConstant.KODO)) {
                returnImage = kodoImageupload.uploadImage(fileMap, userpath, keyID);
            } else if (type.equals(StorageConstant.LOCAL)) {
                returnImage = localImageUpload.uploadImage(fileMap, userpath, keyID);
            } else if (type.equals(StorageConstant.COS)) {
                returnImage = cosImageUpload.uploadImage(fileMap, userpath, keyID);
                ;
            } else if (type.equals(StorageConstant.FTP)) {
                returnImage = ftpImageUpload.uploadImage(fileMap, userpath, keyID);
            } else if (type.equals(StorageConstant.UFILE)) {
                returnImage = uFileImageUpload.uploadImage(fileMap, userpath, keyID);
            } else {
                throw new StorageSourceInitException("GetSource类捕捉异常：未找到存储源");
            }
        } catch (Exception e) {
            log.error("storageSource:error,{}", e.getMessage());
            throw new StorageSourceInitException("GetSource类捕捉异常：", e);
        }
        return returnImage;
    }

    public boolean deleteImg(Integer type, Integer keyId, Images images) {
        boolean isDeleteSuccess = false;
        try {
            if (type.equals(StorageConstant.NOS)) {
                isDeleteSuccess = nosImageUpload.deleteById(keyId, images);
            } else if (type.equals(StorageConstant.OSS)) {
                isDeleteSuccess = ossImageUpload.deleteById(keyId, images);
            } else if (type.equals(StorageConstant.USS)) {
                isDeleteSuccess = ussImageUpload.deleteById(keyId, images);
            } else if (type.equals(StorageConstant.KODO)) {
                isDeleteSuccess = kodoImageupload.deleteById(keyId, images);
            } else if (type.equals(StorageConstant.LOCAL)) {
                isDeleteSuccess = localImageUpload.deleteById(keyId, images);
            } else if (type.equals(StorageConstant.COS)) {
                isDeleteSuccess = cosImageUpload.deleteById(keyId, images);
            } else if (type.equals(StorageConstant.FTP)) {
                isDeleteSuccess = ftpImageUpload.deleteById(keyId, images);
            } else if (type.equals(StorageConstant.UFILE)) {
                isDeleteSuccess = uFileImageUpload.deleteById(keyId, images);
            }
            return isDeleteSuccess;
        } catch (Exception e) {
            log.error("storageSource:error,{}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取存储类型
     * @param key
     * @return
     */
    public Integer getInitType(Keys key) {
        Integer ret = 0;
        if (key.getStorageType() == 1) {
            ret = NOSImageUpload.initialize(key);
        } else if (key.getStorageType() == 2) {
            ret = OSSImageUpload.initialize(key);
        } else if (key.getStorageType() == 3) {
            ret = USSImageUpload.Initialize(key);
        } else if (key.getStorageType() == 4) {
            ret = KODOImageUpload.initialize(key);
        } else if (key.getStorageType() == 6) {
            ret = COSImageUpload.initialize(key);
        } else if (key.getStorageType() == 7) {
            ret = FtpImageUpload.Initialize(key);
        } else if (key.getStorageType() == 8) {
            ret = UFileImageUpload.initialize(key);
        }
        return ret;
    }
}
