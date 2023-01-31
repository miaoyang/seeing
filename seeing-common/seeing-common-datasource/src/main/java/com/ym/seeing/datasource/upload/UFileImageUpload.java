package com.ym.seeing.datasource.upload;

import com.UpYun;
import com.aliyun.oss.model.ObjectMetadata;
import com.ym.seeing.core.domain.Msg;
import com.ym.seeing.core.utils.TextUtil;
import com.ym.seeing.core.utils.TypeDictUtil;
import com.ym.seeing.datasource.domain.Images;
import com.ym.seeing.datasource.domain.Keys;
import com.ym.seeing.datasource.domain.ReturnImage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/13 16:42
 * @Desc:
 */
@Service
@Slf4j
public class UFileImageUpload extends AbstractImageUpload{
    static UpYun uFile;
    static Keys key;

    @Override
    public ReturnImage uploadImage(Map<Map<String, String>, File> fileMap, String username, Integer keyID) {
        ReturnImage returnImage = new ReturnImage();
        File file = null;
        ObjectMetadata meta = new ObjectMetadata();
        meta.setHeader("Content-Disposition", "inline");
        try {
            for (Map.Entry<Map<String, String>, File> entry : fileMap.entrySet()) {
                String ShortUID = TextUtil.getShortUuid();
                file = entry.getValue();
                Msg fileMiME = TypeDictUtil.fileMiME(file);
                meta.setHeader("content-type", fileMiME.getData().toString());
                uFile.setContentMD5(UpYun.md5(file));
                boolean result =
                        uFile.writeFile(
                                username + "/" + ShortUID + "." + entry.getKey(), file, true);
                if (result) {
                    returnImage.setImgname(username + "/" + ShortUID + "." + entry.getKey());
                    returnImage.setImgurl(
                            key.getRequestAddress()
                                    + "/"
                                    + username
                                    + "/"
                                    + ShortUID
                                    + "."
                                    + entry.getKey());
                    returnImage.setImgSize(entry.getValue().length());
                    returnImage.setCode("200");
                } else {
                    returnImage.setCode("400");
                    System.err.println("上传失败");
                }
            }
        } catch (Exception e) {
            returnImage.setCode("500");
        }
        returnImage.setCode("500");
        return returnImage;
    }

    @Override
    public Boolean deleteById(Integer keyID, Images images) {
        boolean b = true;
        try {
            boolean result = uFile.deleteFile(images.getImgName(), null);
        } catch (Exception e) {
            e.printStackTrace();
            b = false;
        }
        return b;
    }

    public static Integer initialize(Keys k) {
        int ret = -1;
        if (k.getAccessSecret() != null
                && k.getAccessKey() != null
                && k.getBucketName() != null
                && k.getRequestAddress() != null) {
            if (!k.getAccessSecret().equals("")
                    && !k.getAccessKey().equals("")
                    && !k.getBucketName().equals("")
                    && !k.getRequestAddress().equals("")) {
                UpYun ufObj = new UpYun(k.getBucketName(), k.getAccessKey(), k.getAccessSecret());
                List<UpYun.FolderItem> items = null;
                try {
                    items = ufObj.readDir("/", null);
                    ret = 1;
                    uFile = ufObj;
                    key = k;
                } catch (Exception e) {
                    System.out.println("UFile Object Is null");
                    ret = -1;
                }
            }
        }
        return ret;
    }
}
