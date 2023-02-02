package com.ym.seeing.api.service;

import com.ym.seeing.api.domain.Code;

import java.util.List;

/**
 * @Author: Yangmiao
 * @Date: 2023/2/1 16:00
 * @Desc:
 */
public interface ICodeService {
    List<Code> selectCode(String value);
    Code selectCodeKey(String code);
    Integer addCode(Code code);
    Integer deleteCode(String code);
}
