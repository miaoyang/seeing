package com.ym.seeing.core.utils;

import com.ym.seeing.core.domain.Msg;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/13 19:37
 * @Desc:
 */
@Slf4j
public class ImgUrlUtil {
    /**
     * 从网络Url中下载文件
     *
     * @param urlStr
     * @param fileName
     * @param savePath
     * @throws IOException
     */
    public static Map<String, Object> downLoadFromUrl(String urlStr, String fileName, String savePath) {

        Map<String, Object> resmap = new HashMap<>();
        Map<String, String> map = checkURLStatusCode(urlStr);
        if (map.size() == 0 || map.get("Check").equals("false")) {
            resmap.put("res", false);
            resmap.put("StatusCode", map.get("StatusCode"));
            return resmap;
        }
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(5 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            //得到输入流
            InputStream inputStream = conn.getInputStream();
            //获取自己数组
            byte[] getData = readInputStream(inputStream);
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdir();
            }
            File file = new File(saveDir + File.separator + fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(getData);
            if (fos != null) {
                fos.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            //下载并且保存成功后 判断格式 如果不是图像格式 就删除
//
            if (new File(saveDir + File.separator + fileName).exists()) {
                Msg msg = TypeDictUtil.fileMiME(file);
                if (msg.getCode().equals("200")) {
                    File f = new File(saveDir + File.separator + fileName);
                    String imgPath = saveDir + File.separator + fileName + "." +
                            (msg.getData().toString().replace("image/", ""));
                    f.renameTo(new File(imgPath));
                    resmap.put("res", true);
                    resmap.put("imgPath", imgPath);
                    resmap.put("imgsize", new File(imgPath).length());
                } else {
                    File deleteFile = new File(saveDir + File.separator + fileName);
                    boolean delete = deleteFile.delete();
                    log.debug("删除文件: isSuccess={}",delete);
                    resmap.put("res", false);
                    resmap.put("StatusCode", "110403");
                }
            } else {
                resmap.put("res", false);
                resmap.put("StatusCode", "500");
            }
            return resmap;
        } catch (Exception e) {
            e.printStackTrace();
            resmap.put("res", false);
            resmap.put("StatusCode", "500");
            return resmap;
        }
    }

    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    /**
     * 检测Url的响应值
     * 判断是否能访问。
     * @param urlStr
     * @return
     */
    public static Map<String, String> checkURLStatusCode(String urlStr) {
        Map<String, String> map = new HashMap<>();
        try {
            URL url = new URL(urlStr);
            URLConnection rulConnection = url.openConnection();
            HttpURLConnection httpUrlConnection = (HttpURLConnection) rulConnection;
            httpUrlConnection.setConnectTimeout(300000);
            httpUrlConnection.setReadTimeout(300000);
            httpUrlConnection.connect();
            String code = new Integer(httpUrlConnection.getResponseCode()).toString();
            String message = httpUrlConnection.getResponseMessage();
            log.info("getResponseCode code =" + code);
            log.info("getResponseMessage message =" + message);
            if (!code.startsWith("2") && !code.startsWith("3")) {
                map.put("Check", "false");
                map.put("StatusCode", code);
                throw new Exception("ResponseCode is not begin with 2,code=" + code);
            }
            map.put("Check", "true");
            log.info("连接正常");
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return map;
    }
}
