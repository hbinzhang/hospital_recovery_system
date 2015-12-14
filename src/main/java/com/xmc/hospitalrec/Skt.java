package com.xmc.hospitalrec;

import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Skt {

	private static final Logger LOGGER = LoggerFactory.getLogger( Skt.class);

	private Skt() {
	}

	private static final Preferences USER_PREFS = Preferences.userNodeForPackage( Skt.class);
	private static final Preferences SYSTEM_PREFS = Preferences.systemNodeForPackage( Skt.class);
	private static final String PROFILE = System.getProperty( Skt.class.getPackage().getName() + ".profile", "").trim();
	private static final Preferences PROFILE_USER_PREFS = PROFILE.equals( "")? USER_PREFS: USER_PREFS.node( PROFILE);
	private static final Preferences PROFILE_SYSTEM_PREFS = PROFILE.equals( "")? SYSTEM_PREFS: SYSTEM_PREFS.node( PROFILE);
	static {
		LOGGER.info( "profile = " + PROFILE);
	}

	public static String getProfile() {
		return PROFILE;
	}

	public static boolean isDevelopmentProfile() {
		boolean isProfile = Pattern.matches( "development(\\..*)?", PROFILE);
		System.out.println("isDevelopmentProfile : " + isProfile);
		return isProfile;
	}

	public static String getDatabaseUrl() {
//		String dbUrl = Skt.getConfiguration( "database-url", isDevelopmentProfile()? "jdbc:hsqldb:mem:nfvo"
//				: "jdbc:mysql://root:mysql@localhost/nfvo");
		String dbUrl = "jdbc:mysql://root:mysql@localhost/hospitalrec?useUnicode=true&characterEncoding=utf-8";
//		System.out.println("dbUrl : " + dbUrl);
		return dbUrl;
	}

	public static String getConfiguration( final String key, final String defaultValue) {
		return PROFILE_USER_PREFS.get( key, USER_PREFS.get( key, PROFILE_SYSTEM_PREFS.get( key, SYSTEM_PREFS.get( key, defaultValue))));
	}
}
