package com.dagl.datahandle.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;


/**
 * @author Administrator
 * @className HibernateConfig
 * @description TODO
 * @date 2019/03/22
 */
@Configuration
@EnableTransactionManagement
public class HibernateConfig {

    @Value("${spring.datasource.driverClassName}")
    private String DRIVER;

    @Value("${spring.datasource.password}")
    private String PASSWORD;

    @Value("${spring.datasource.url}")
    private String URL;

    @Value("${spring.datasource.username}")
    private String USERNAME;

    @Value("${spring.jpa.properties.hibernate.dialect}")
    private String DIALECT;

    @Value("${spring.jpa.show-sql}")
    private String SHOW_SQL;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String HBM2DDL_AUTO;

    @Value("${entitymanager.packagesToScan}")
    private String PACKAGES_TO_SCAN;

    /**
     * @return
     */
    @Bean(value = "sessionFactory")
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan(PACKAGES_TO_SCAN);
        Properties hibernateProperties = new Properties();
        hibernateProperties.put("hibernate.dialect", DIALECT);
        hibernateProperties.put("hibernate.show_sql", SHOW_SQL);
        hibernateProperties.put("hibernate.hbm2ddl.auto", HBM2DDL_AUTO);
        sessionFactory.setHibernateProperties(hibernateProperties);

        return sessionFactory;
    }

    /**
     * @return
     */
    @Bean
    public DruidDataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(DRIVER);
        dataSource.setUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setInitialSize(10);
        dataSource.setMinIdle(10);
        dataSource.setMaxActive(100);
        return dataSource;
    }


}
