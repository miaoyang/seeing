package com.ym.seeing.datasource.upload;

import com.ym.seeing.datasource.constant.StorageConstant;
import com.ym.seeing.datasource.domain.Images;
import com.ym.seeing.datasource.domain.ReturnImage;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Map;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/13 16:58
 * @Desc: 本地资源上传到服务器
 */
@Component
public class LocalImageUpload extends AbstractImageUpload{

    @Override
    public ReturnImage uploadImage(Map<Map<String, String>, File> fileMap, String username, Integer keyID) {
        ReturnImage returnImage = new ReturnImage();
        String filePath = StorageConstant.LOCPATH + File.separator;
        try {
            for (Map.Entry<Map<String, String>, File> entry : fileMap.entrySet()) {
                String prefix = entry.getKey().get("prefix");
                String ShortUIDName = entry.getKey().get("name");
                File dest = new File(filePath + username + File.separator + ShortUIDName + "." + prefix);
                if (!dest.getParentFile().exists()) {
                    dest.getParentFile().mkdirs();
                }
                InputStream fileInputStream = new FileInputStream(entry.getValue());
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dest));
                byte[] bs = new byte[1024];
                int len;
                while ((len = fileInputStream.read(bs)) != -1) {
                    bos.write(bs, 0, len);
                }
                bos.flush();
                bos.close();
            }
            returnImage.setCode("200");
        } catch (Exception e) {
            e.printStackTrace();
            returnImage.setCode("500");
            System.err.println("上传失败");
        }
        return returnImage;
    }

    @Override
    public Boolean deleteById(Integer keyID, Images images) {
        boolean isDelete = false;
        try {
            String filePath = StorageConstant.LOCPATH + File.separator+images.getImgName();
            File file = new File(filePath);
            if(file.exists()){
                isDelete = file.delete();
            }else{
                isDelete = true;
            }
        }catch (Exception e){
            e.printStackTrace();
            isDelete = false;
        }
        return isDelete;
    }
}
