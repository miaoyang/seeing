package com.ym.seeing.api.service;

import com.ym.seeing.core.domain.Msg;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/12 19:24
 * @Desc: 上传文件
 */
public interface IUploadService {
    public Msg uploadForLoc(
            HttpServletRequest request,
            MultipartFile multipartFile,
            Integer setDay,
            String imgUrl);
}
