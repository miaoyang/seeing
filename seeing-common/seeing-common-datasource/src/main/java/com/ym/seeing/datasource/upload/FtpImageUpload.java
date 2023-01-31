package com.ym.seeing.datasource.upload;

import cn.hutool.extra.ftp.Ftp;
import cn.hutool.extra.ftp.FtpMode;
import com.ym.seeing.datasource.domain.Images;
import com.ym.seeing.datasource.domain.Keys;
import com.ym.seeing.datasource.domain.ReturnImage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.apache.commons.net.ftp.FTP;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/13 16:31
 * @Desc:
 */
@Service
@Slf4j
public class FtpImageUpload extends AbstractImageUpload{
    private Keys key;

    @Override
    public ReturnImage uploadImage(Map<Map<String, String>, File> fileMap, String username, Integer keyID) {
        ReturnImage returnImage = new ReturnImage();
        Ftp ftp = null;
        try {
            if (null == key) {
                System.out.println("FTP = 存储源对象为空");
                returnImage.setCode("500");
                return returnImage;
            }
            Charset charset = StandardCharsets.UTF_8;
            String[] hostArr = key.getEndpoint().split("\\:");
            String host = hostArr[0];
            Integer port = Integer.parseInt(hostArr[1]);
            ftp = new Ftp(host, port, key.getAccessKey(), key.getAccessSecret(), charset);
            ftp.getClient().type(FTP.BINARY_FILE_TYPE);
            ftp.setMode(FtpMode.Passive);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("FTP = 存储源对象为空");
            returnImage.setCode("500");
            return returnImage;
        }
        File file = null;
        try {
            for (Map.Entry<Map<String, String>, File> entry : fileMap.entrySet()) {
                String prefix = entry.getKey().get("prefix");
                String ShortUIDName = entry.getKey().get("name");
                file = entry.getValue();
                boolean isUpload = ftp.upload(
                        "/" + username,
                        ShortUIDName + "." + prefix,
                        file);
                if (!isUpload) {
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
        Ftp ftp = null;
        try {
            Charset charset = StandardCharsets.UTF_8;
            String[] hostArr = key.getEndpoint().split("\\:");
            String host = hostArr[0];
            Integer port = Integer.parseInt(hostArr[1]);
            ftp = new Ftp(host, port, key.getAccessKey(), key.getAccessSecret(), charset);
            b = ftp.delFile(images.getImgName());
        } catch (Exception e) {
            e.printStackTrace();
            b = false;
        }
        return b;
    }

    public static int Initialize(Keys k) {
        int ret = -1;
        try{
            if (StringUtils.isBlank(k.getAccessKey())
                    || StringUtils.isBlank(k.getAccessSecret())
                    || StringUtils.isBlank(k.getEndpoint())
                    || StringUtils.isBlank(k.getRequestAddress())){
                return -1;
            }
            Charset charset = StandardCharsets.UTF_8;
            String[] hostArr = k.getEndpoint().split("\\:");
            String host = hostArr[0];
            Integer port = Integer.parseInt(hostArr[1]);
            Ftp ftp = new Ftp(host, port, k.getAccessKey(), k.getAccessSecret(), charset);
            ftp.getClient().type(FTP.BINARY_FILE_TYPE);
            ftp.setMode(FtpMode.Passive);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
