package com.ym.seeing.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ym.seeing.api.domain.Code;
import com.ym.seeing.api.mapper.CodeMapper;
import com.ym.seeing.api.service.ICodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Yangmiao
 * @Date: 2023/2/1 16:01
 * @Desc:
 */
@Service
public class CodeServiceImpl implements ICodeService {
    @Autowired
    private CodeMapper codeMapper;

    @Override
    public List<Code> selectCode(String value) {
        LambdaQueryWrapper<Code> wrapper = new LambdaQueryWrapper<>();
        if (value !=null){
            wrapper.eq(Code::getCode,value);
        }
        return codeMapper.selectList(wrapper);
    }

    @Override
    public Code selectCodeKey(String code) {
        LambdaQueryWrapper<Code> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Code::getCode,code);
        return codeMapper.selectOne(wrapper);
    }

    @Override
    public Integer addCode(Code code) {
        return codeMapper.insert(code);
    }

    @Override
    public Integer deleteCode(String code) {
        LambdaQueryWrapper<Code> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Code::getCode,code);
        return codeMapper.delete(wrapper);
    }
}
