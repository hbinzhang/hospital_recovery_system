package com.ericsson.skt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configure {
	private static final Logger LOGGER = LoggerFactory.getLogger(Configure.class);
	private static final Properties properties = new Properties();
	
	public static final String VIMGW_URL ="vimgw_url";
	public static final String CMGW_URL ="cmgw_url";
	public static final String RCGW_URL ="rcgw_url";
	public static final String RCGW_ID ="rcgw_id";
	public static final String NFVO_ID ="nfvo_id";
	public static final String VIM_ID ="vim_id";
	public static final String VNF_INSTANCE_ID ="vnfInstanceID";
	public static final String VNFM_ID = "vnfm_id";
	public static final String PMJOB_PERIOD = "pmjob_period";
	public static final String ALARM_REMAIN_DAYS = "alarm_remain_days";
	public static final String TOPO_UPDATE_PERIOD = "topo_update_period";
	
	public static final String VEPG_HOST_IP = "VEPG_HOST_IP";
	public static final String VEPG_HOST_USERNAME = "VEPG_HOST_USERNAME";
	public static final String VEPG_HOST_PASSWORD = "VEPG_HOST_PASSWORD";
	public static final String VEPG_HOST_SCRIPT_DIR = "VEPG_HOST_SCRIPT_DIR";
	public static final String VEPG_HOST_TUN_USERNAME = "VEPG_HOST_TUN_USERNAME";
	public static final String VEPG_HOST_TUN_PASSWORD = "VEPG_HOST_TUN_PASSWORD";
	public static final String VEPG_HOST_OUTPUT_FILE_NAME = "VEPG_HOST_OUTPUT_FILE_NAME";
	
	static {
		reload();
	}
	
	public static void reload() {
		try {
			String userDir = System.getProperty("user.dir");
			LOGGER.info("load config.properties from: " + userDir);
			//properties.load(Configure.class.getClassLoader().getResourceAsStream("config.properties"));
			properties.load(new FileInputStream(userDir + File.separator + "config.properties"));
		} catch (IOException ex) {
			LOGGER.error("load from config.properties fail", ex);
		}
	}
	
	public static String getConfigValue(String configKey) {
		return properties.getProperty(configKey);
	}
	
	public static String getVnfInstanceId() {
		return getConfigValue(VNF_INSTANCE_ID);
	}
	
	public static String getCmgwUrl() {
		return getConfigValue(CMGW_URL);
	}

	public static String getVimgwUrl() {
		return getConfigValue(VIMGW_URL);
	}

	public static String getRcgwUrl() {
		return getConfigValue(RCGW_URL);
	}

	public static String getVimId() {
		return getConfigValue(VIM_ID);
	}

	public static String getNfvoId() {
		return getConfigValue(NFVO_ID);
	}

	public static String getRcgwId() {
		return getConfigValue(RCGW_ID);
	}

	public static String getVnfmId() {
		return getConfigValue(VNFM_ID);
	}

	public static String getPmJobPeriod() {
		return getConfigValue(PMJOB_PERIOD);
	}

	public static int getAlarmRemainDays() {
		return Integer.parseInt(getConfigValue(ALARM_REMAIN_DAYS));
	}
	
	public static int getTopoUpdatePeriod() {
		return Integer.parseInt(getConfigValue(TOPO_UPDATE_PERIOD));
	}
}
