package com.ericsson.skt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import com.ericsson.skt.model._ModelPackage;

public class OrmFactory extends LocalContainerEntityManagerFactoryBean {

	private static final String[] PACKAGES_TO_SCAN = { _ModelPackage.class.getPackage().getName()};

	private final HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();

	public OrmFactory( String databaseUrl) {
		Matcher matcher = Pattern.compile( "^jdbc:(mysql|hsqldb:hsql|hsqldb:file|hsqldb:mem):(.*)$").matcher( databaseUrl);
		if( !matcher.find()) throw new IllegalArgumentException( "unrecognized database URL: " + databaseUrl);
		switch( matcher.group( 1)) {
			case "mysql":
				Matcher mysqlMatcher = Pattern.compile( "^//(.+):(.*)@(.+)(?::(\\d+))?/(.+)$").matcher( matcher.group( 2));
				if( !mysqlMatcher.find()) throw new IllegalArgumentException( "unrecognized MySQL database URL: " + databaseUrl);
				String mysqlUser = mysqlMatcher.group( 1);
				String mysqlPass = mysqlMatcher.group( 2);
				String mysqlHost = mysqlMatcher.group( 3);
				String mysqlPort = mysqlMatcher.group( 4) == null? "3306": mysqlMatcher.group( 4);
				String mysqlName = mysqlMatcher.group( 5);
				this.jpaVendorAdapter.setDatabase( Database.MYSQL);
				this.jpaVendorAdapter.setDatabasePlatform( MySQL5InnoDBDialect.class.getName());
				this.setProperty( "hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
				this.setProperty( "hibernate.connection.url", "jdbc:mysql://" + mysqlHost + ":" + mysqlPort + "/" + mysqlName);
				this.setProperty( "hibernate.connection.username", mysqlUser);
				this.setProperty( "hibernate.connection.password", mysqlPass);
				break;
			case "hsqldb:hsql":
				Matcher hsqlMatcher = Pattern.compile( "^//(.+):(.*)@(.+)(?::(\\d+))?/(.+)$").matcher( matcher.group( 2));
				if( !hsqlMatcher.find()) throw new IllegalArgumentException( "invalid HSQLDB URL: " + databaseUrl);
				String hsqlUser = hsqlMatcher.group( 1);
				String hsqlPass = hsqlMatcher.group( 2);
				String hsqlHost = hsqlMatcher.group( 3);
				String hsqlPort = hsqlMatcher.group( 4) == null? "9001": hsqlMatcher.group( 4);
				String hsqlName = hsqlMatcher.group( 5);
				this.jpaVendorAdapter.setDatabase( Database.HSQL);
				this.jpaVendorAdapter.setDatabasePlatform( HSQLDialect.class.getName());
				this.setProperty( "hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
				this.setProperty( "hibernate.connection.url", "jdbc:hsqldb:hsql://" + hsqlHost + ":" + hsqlPort + "/" + hsqlName);
				this.setProperty( "hibernate.connection.username", hsqlUser);
				this.setProperty( "hibernate.connection.password", hsqlPass);
				break;
			case "hsqldb:file":
			case "hsqldb:mem":
				this.jpaVendorAdapter.setDatabase( Database.HSQL);
				this.jpaVendorAdapter.setDatabasePlatform( HSQLDialect.class.getName());
				this.setProperty( "hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
				this.setProperty( "hibernate.connection.url", databaseUrl);
				this.setProperty( "hibernate.connection.username", "sa");
				this.setProperty( "hibernate.connection.password", "");
				break;
		}
		this.setJpaDialect( new HibernateJpaDialect());
		this.setJpaVendorAdapter( this.jpaVendorAdapter);
		this.setPersistenceUnitName( "skt");
		this.setPackagesToScan( PACKAGES_TO_SCAN);
		this.setProperty( "hibernate.c3p0.minSize", "0");
		this.setProperty( "hibernate.c3p0.maxSize", "64");
		this.setProperty( "hibernate.c3p0.acquireIncrement", "1");
		this.setProperty( "hibernate.c3p0.idleTestPeriod", "500");
		this.setProperty( "hibernate.c3p0.maxStatements", "0");
		this.setProperty( "hibernate.c3p0.timeout", "100");
	}

	public OrmFactory() {
		this( "jdbc:mysql://sa:@localhost/skt");
		this.setProperty( "hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
		this.setProperty( "hibernate.connection.url", "jdbc:hsqldb:mem:skt");
	}

	public void setProperty( String key, String value) {
		this.getJpaPropertyMap().put( key, value);
	}

	public void generateDdl() {
		jpaVendorAdapter.setGenerateDdl( true);
	}

	public void showSql() {
		jpaVendorAdapter.setShowSql( true);
	}
}
