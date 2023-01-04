package com.ym.seeing.api.shiro;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.ym.seeing.api.constant.UserConstant;
import com.ym.seeing.api.service.impl.UserServiceImpl;
import com.ym.seeing.api.util.SpringUtil;
import com.ym.seeing.core.domain.User;
import com.ym.seeing.core.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/20 21:48
 * @Desc:
 */
@Slf4j
public class SubjectFilter extends BasicHttpAuthenticationFilter {
    public static String WEBHOST = null;
    private String CODE = "000";

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        UserServiceImpl userService = SpringUtil.getBean(UserServiceImpl.class);
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String serviceName = httpServletRequest.getServletPath();
        String userOrigin = httpServletRequest.getHeader(UserConstant.USER_ORIGIN);
        String token = httpServletRequest.getHeader(UserConstant.AUTHORIZATION);
        if (httpServletRequest.getMethod().equals("POST") && !serviceName.contains("/api") && !serviceName.contains("/verifyCode")
                && !serviceName.contains("/getClientVersion") && !serviceName.contains("/client")) {
            try {
                if (userOrigin.compareTo(SecureUtil.md5(WEBHOST)) != 0) {
                    log.error("前端域名校验未通过");
                    log.error("request-MD5:" + userOrigin);
                    log.error("配置文件-MD5:" + SecureUtil.md5(WEBHOST));
                    log.error("配置Host:" + WEBHOST);
                    this.CODE = "406";
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.CODE = "500";
                return false;
            }
        }
        if (serviceName.contains("/client")) {
            token = httpServletRequest.getHeader("jwttoken");
            String userToken = httpServletRequest.getHeader("userToken");
            User user = new User();
            user.setToken(userToken);
            User userData = userService.loginByToken(userToken);
            if (null == userData) {
                this.CODE = "110404";
                return false;
            } else {
                if (userData.getIsOk() < 1) {
                    this.CODE = "110403";
                    return false;
                }
                Subject sub = SecurityUtils.getSubject();
                User u = (User) sub.getPrincipal();
                if (null == u) {
                    httpServletRequest.getSession().setAttribute("user", userData);
                    Subject subject = SecurityUtils.getSubject();
                    UsernamePasswordToken tokenOBJ = new UsernamePasswordToken(userData.getEmail(), userData.getPassword());
                    tokenOBJ.setRememberMe(true);
                    subject.login(tokenOBJ);
                    SecurityUtils.getSubject().getSession().setTimeout(3600000);
                    User loginUser = (User) SecurityUtils.getSubject().getPrincipal();
                }
            }
        }
        JSONObject jsonObject = JWTUtil.checkToken(token);
        if (!jsonObject.getBoolean("check")) {
            if (!serviceName.contains("admin") || serviceName.contains("admin/client")) {
                return true;
            } else {
                this.CODE = "403";
                return false;
            }
        } else {
            Subject subject = SecurityUtils.getSubject();
            User user = (User) subject.getPrincipal();
            if (user == null) {
                UsernamePasswordToken tokenOBJ = new UsernamePasswordToken(jsonObject.getString("email"), jsonObject.getString("password"));
                tokenOBJ.setRememberMe(true);
                try {
                    subject.login(tokenOBJ);
                    SecurityUtils.getSubject().getSession().setTimeout(3600000);
                } catch (Exception e) {
                    this.CODE = "403";
                    return false;
                }
            } else {
                if (null != user) {
                    try {
                        if (null != user.getId()) {
                            if (userService.getUsers(user).getIsOk() < 1) {
                                subject.logout();
                                this.CODE = "403";
                                return false;
                            }
                        }
                    } catch (Exception e) {
                        log.error("拦截器判断用户isOK的时候报错了");
                        e.printStackTrace();
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        String info = "未知错误";
        try {
            if (this.CODE.equals("406")) {
                info = "前端域名配置不正确";
            } else if (this.CODE.equals("403")) {
                info = "当前用户无权访问该请求";
            } else if (this.CODE.equals("402")) {
                info = "当前web请求不合规";
            } else if (this.CODE.equals("110403")) {
                info = "该账户暂时无法使用";
            } else if (this.CODE.equals("110404")) {
                info = "用户未找到";
            }
            log.error("拦截器False-" + info);
            response.setContentType("application/json;charset=UTF-8");
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", this.CODE);
            jsonObject.put("info", info);
            response.getWriter().write(jsonObject.toJSONString());
        } catch (Exception e) {
            log.error("返回token验证失败403请求，报异常了");
        }

        return false;
    }
}
