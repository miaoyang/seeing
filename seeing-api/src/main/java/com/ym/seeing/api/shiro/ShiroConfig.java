package com.ym.seeing.api.shiro;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/20 21:40
 * @Desc:
 */
@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ShiroConfig {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public SubjectFilter subjectFilter(){
        return new SubjectFilter();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier("defaultWebSecurityManager") DefaultWebSecurityManager defaultWebSecurityManager) {
        // 1.创建过滤器工厂
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        // 2.设置安全管理器
        bean.setSecurityManager(defaultWebSecurityManager);
        Map<String, Filter> beanFilters = bean.getFilters();
//        beanFilters.put("subjectFilter",subjectFilter());
        beanFilters.put("authc",new CrosUserFilter());
        bean.setFilters(beanFilters);
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
        // 监控接口白名单
        filterMap.put("/actuator/**","anon");
        filterMap.put("/applications/*","anon");
        // swagger
        filterMap.put("/doc.html","anon");
        filterMap.put("/webjars/**","anon");
        filterMap.put("/swagger-ui.html", "anon");
        filterMap.put("/swagger-resources", "anon");
        filterMap.put("/v2/api-docs", "anon");
        filterMap.put("/v2/api-docs-ext", "anon");
        filterMap.put("/configuration/security", "anon");
        filterMap.put("/configuration/ui", "anon");


        filterMap.put("/test","anon");
        filterMap.put("/verifyCode","anon");
        filterMap.put("/verifyCodeForRegister","anon");
        filterMap.put("/verifyCodeForRetrieve","anon");
        filterMap.put("/api/**","anon");
        filterMap.put("/user/**","anon");
        filterMap.put("/ota/**","anon");
        filterMap.put("/admin/root/**","roles[admin]");
//        filterMap.put("/**","subjectFilter");
        filterMap.put("/**","authc");
        // 登录失败跳转方式
        bean.setLoginUrl("/jurisError");
        // 认证失败跳转
        bean.setUnauthorizedUrl("/authError");
        bean.setFilterChainDefinitionMap(filterMap);
        return bean;
    }

    @Bean(name = "defaultWebSecurityManager")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
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
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public UserRealm userRealm(){
        return new UserRealm();
    }


    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public static DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator=new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setUsePrefix(true);
        return defaultAdvisorAutoProxyCreator;
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition(){
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        chainDefinition.addPathDefinition("/doc.html", "anon");
        chainDefinition.addPathDefinition("/webjars/**", "anon");
        return chainDefinition;
    }
}
