package com.ym.seeing.core.utils;

import com.ym.seeing.core.domain.Msg;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;

import java.io.File;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/13 16:20
 * @Desc:
 */
@Slf4j
public class TypeDictUtil {

    public static Msg fileMiME(File file ){
        final Msg msg = new Msg();
        try {
            Tika tika = new Tika();
            String fileType = tika.detect(file);
            if (fileType != null && fileType.contains("/")) {
                if(fileType.contains("image/")){
                    msg.setData(fileType);
                }else{
                    //非图像类型
                    msg.setCode("110602");
                    msg.setInfo("该文件非图像文件，或不受支持");
                }
            }
        } catch (Exception e) {
            log.error("这是一个图像类别鉴定的报错:161");
            msg.setCode("110603");
            msg.setInfo("暂时不能上传该文件");
        }
        return msg;
    }
}
