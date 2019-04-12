package com.dagl.datahandle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class,
		scanBasePackages = {"com.dagl.datahandle"})
public class DatahandleApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(DatahandleApplication.class, args);
	}

	/**
	 * war打包部署
	 * @param application
	 * @return
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(DatahandleApplication.class);
	}
}
