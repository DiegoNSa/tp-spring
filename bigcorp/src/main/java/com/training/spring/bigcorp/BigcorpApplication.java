package com.training.spring.bigcorp;

import com.training.spring.bigcorp.config.properties.BigCorpApplicationProperties;
import com.training.spring.bigcorp.service.SiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableConfigurationProperties
public class BigcorpApplication {

	private final static Logger logger = LoggerFactory.getLogger(BigcorpApplication.class);

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(BigcorpApplication.class, args);

		BigCorpApplicationProperties bigCorpApplicationProperties = context.getBean(BigCorpApplicationProperties.class);

		logger.info("===============================================================================");
		logger.info("Application [" + bigCorpApplicationProperties.getName() + "] - version : " + bigCorpApplicationProperties.getVersion());
		logger.info("Plus d'information sur " + bigCorpApplicationProperties.getWebSiteUrl());
		logger.info("===============================================================================");

		SiteService siteServiceA = context.getBean(SiteService.class);
		logger.info(siteServiceA.findById("siteA").toString());
		SiteService siteServiceB = context.getBean(SiteService.class);
		logger.info("" + siteServiceB.findById("siteA").toString());
	}

}
