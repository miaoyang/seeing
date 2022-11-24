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
    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier("defaultWebSecurityManager") DefaultWebSecurityManager defaultWebSecurityManager) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(defaultWebSecurityManager);
        Map<String, Filter> beanFilters = bean.getFilters();
        beanFilters.put("JWT",new SubjectFilter());
        Map<String,String> filterMap = new LinkedHashMap<>();
        filterMap.put("/verifyCode","anon");
        filterMap.put("/verifyCodeForRegister","anon");
        filterMap.put("/verifyCodeForRetrieve","anon");
        filterMap.put("/api/**","anon");
        filterMap.put("/user/**","anon");
        filterMap.put("/ota/**","anon");
        filterMap.put("/admin/root/**","roles[admin]");
        filterMap.put("/**","JWT");
        bean.setLoginUrl("/jurisError");
        bean.setUnauthorizedUrl("/authError");
        bean.setFilterChainDefinitionMap(filterMap);
        return bean;
    }

    @Bean
    public DefaultWebSecurityManager defaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm){
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(userRealm);
        defaultWebSecurityManager.setRememberMeManager(null);
        return defaultWebSecurityManager;
    }

    @Bean(name = "userRealm")
    public UserRealm userRealm(){
        return new UserRealm();
    }

}
