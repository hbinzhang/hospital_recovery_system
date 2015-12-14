package com.xmc.hospitalrec;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.persistence.EntityManagerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.xmc.hospitalrec.ApplicationContextInitializer;
import com.xmc.hospitalrec.dao.BaseDao;
import com.xmc.hospitalrec.rest._RestPackage;

@ Configuration
@ EnableTransactionManagement
// in org.glassfish.jersey.server.spring.SpringComponentProvider#bind( Class, Set), jersey-spring3 looks for spring-managed beans via
// ctx.getBeanNamesForType(component), which returns an empty String array when dynamic proxy is used. with this annotation we are forcing
// Spring to use CGLib.
@ EnableAspectJAutoProxy( proxyTargetClass = true)
@ EnableScheduling
@ ComponentScan( basePackageClasses = { _RestPackage.class, BaseDao.class})
public class ApplicationContext {

	@ Bean
	public PlatformTransactionManager transactionManager( final EntityManagerFactory entityManagerFactory) {
		final JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory( entityManagerFactory);
		return transactionManager;
	}

	@ Bean
	public LocalContainerEntityManagerFactoryBean hibernateEntityManagerFactoryBean() {
		String dbUrl = Skt.getDatabaseUrl();
//		System.out.println("dbUrl: " + dbUrl);
		OrmFactory ormFactory = new OrmFactory( dbUrl );
//		if( Skt.isDevelopmentProfile()) {
			ormFactory.generateDdl();
			ormFactory.showSql();
//		}
		return ormFactory;
	}

	@ Bean
	public ApplicationContextInitializer applicationContextInitializer() {
		return new ApplicationContextInitializer();
	}

	@ Bean( destroyMethod = "shutdown")
	public ScheduledExecutorService scheduledExecutorService() {
		return Executors.newSingleThreadScheduledExecutor();
	}
	
	@ Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource rbms = new ResourceBundleMessageSource();
		rbms.setBasename("message");
		rbms.setDefaultEncoding("UTF-8");
		return rbms;
	}
}
