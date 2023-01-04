package com.ym.seeing.api.util;

import com.ym.seeing.core.exception.GlobalException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Author: Yangmiao
 * @Date: 2022/12/19 17:05
 * @Desc: 获取Spring的一些属性
 */
@Component
public class SpringUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.applicationContext = applicationContext;
    }

    public static <T> T getBean(String beanName){
        assertApplicationContext();
        return (T) applicationContext.getBean(beanName);
    }

    public static <T> T getBean(Class<T> classType){
        assertApplicationContext();
        return applicationContext.getBean(classType);
    }

    private static void assertApplicationContext(){
        if (SpringUtil.applicationContext == null){
            throw new GlobalException("ApplicationContext为null，请检查是否成功注入！");
        }
    }
}
