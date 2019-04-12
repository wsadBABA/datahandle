package com.dagl.datahandle.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2018-11-6.
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext(){
        return  SpringContextUtil.applicationContext;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name){
        return (T) applicationContext.getBean(name);
    }
}
