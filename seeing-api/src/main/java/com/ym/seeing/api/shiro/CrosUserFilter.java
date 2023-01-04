package com.ym.seeing.api.shiro;

import com.alibaba.fastjson.JSON;
import com.ym.seeing.api.domain.vo.Result;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: Yangmiao
 * @Date: 2022/12/20 10:00
 * @Desc:
 */
@Configuration
public class CrosUserFilter extends UserFilter {

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())){
            setHeaders(httpServletRequest,httpServletResponse);
            return true;
        }
        return super.preHandle(request, response);
    }

    private void setHeaders(HttpServletRequest request, HttpServletResponse response) {
        //跨域的header设置
        response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods", request.getMethod());
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
        //防止乱码，适用于传输JSON数据
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        response.setStatus(HttpStatus.OK.value());

    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        if (httpServletRequest.getMethod().toUpperCase().equals(RequestMethod.OPTIONS.name())){
            return true;
        }
        return super.isAccessAllowed(request, response, mappedValue);
    }
}
