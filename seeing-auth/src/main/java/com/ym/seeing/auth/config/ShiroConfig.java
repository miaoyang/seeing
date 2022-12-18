package com.ym.seeing.auth.config;

import com.ym.seeing.auth.filter.SubjectFilter;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/20 21:40
 * @Desc:
 */
@Configuration
public class ShiroConfig {

    @Bean
    public SubjectFilter subjectFilter(){
        return new SubjectFilter();
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier("defaultWebSecurityManager") DefaultWebSecurityManager defaultWebSecurityManager) {
        // 1.创建过滤器工厂
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        // 2.设置安全管理器
        bean.setSecurityManager(defaultWebSecurityManager);
//        Map<String, Filter> beanFilters = bean.getFilters();
//        beanFilters.put("subjectFilter",subjectFilter());
//        bean.setFilters(beanFilters);
        // 设置过滤器集合
        /**
         *  * 常用的过滤器
         *  * anon: 无需认证(登录)即可访问
         *  * authc: 必须认证才可访问
         *  * user: 如果使用 rememberMe
         *  * perms: 该资源必须得到资源权限才能访问
         *  * role: 该资源必须得到角色权限才可访问
         */
        Map<String,String> filterMap = new LinkedHashMap<>();
        filterMap.put("/verifyCode","anon");
        filterMap.put("/verifyCodeForRegister","anon");
        filterMap.put("/verifyCodeForRetrieve","anon");
        filterMap.put("/api/**","anon");
        filterMap.put("/user/**","anon");
        filterMap.put("/ota/**","anon");
        filterMap.put("/admin/root/**","roles[admin]");
        filterMap.put("/**","JWT");
        // 登录失败跳转方式
        bean.setLoginUrl("/jurisError");
        // 认证失败跳转
        bean.setUnauthorizedUrl("/authError");
        bean.setFilterChainDefinitionMap(filterMap);
        return bean;
    }

    @Bean(name = "defaultWebSecurityManager")
    public DefaultWebSecurityManager defaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm){
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(userRealm);
        return defaultWebSecurityManager;
    }

    /**
     * Realm对象
     * @return
     */
    @Bean(name = "userRealm")
    public UserRealm userRealm(){
        return new UserRealm();
    }

    /**
     * 取消自动注册自定义filter
     */
//    @Bean
//    public FilterRegistrationBean<AccessFilter> accessFilterRegistration(
//            AccessFilter accessFilter) {
//        FilterRegistrationBean<AccessFilter> filterRegistrationBean = new
//                FilterRegistrationBean<>(accessFilter);
//        filterRegistrationBean.setEnabled(false);
//        return filterRegistrationBean;
//    }

}
