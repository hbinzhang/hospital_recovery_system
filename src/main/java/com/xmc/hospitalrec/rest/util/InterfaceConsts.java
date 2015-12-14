package com.xmc.hospitalrec.rest.util;

public class InterfaceConsts {
	// QueryVNFnetwork
	public static final String NETWORK_VNLIST = "vnlist";
	public static final String NETWORK_ID = "id";
	public static final String NETWORK_NAME = "name";
	public static final String NETWORK_DESCRIPTION = "description";
	public static final String NETWORK_IPADDRESSRANGE = "ipAddressRange";
	public static final String NETWORK_DHCPENABLED = "dhcpEnabled";
	public static final String NETWORK_IPVERSION = "ipVersion";
	public static final String NETWORK_CATEGORY = "category";
	public static final String NETWORK_NETWORKTYPE = "networkType";
	public static final String NETWORK_PROVISIONINGSTATUS = "provisioningStatus";
	public static final String NETWORK_ENABLED = "enabled";
	public static final String NETWORK_VMLIST = "vmlist";
	public static final String NETWORK_VIMOBJECTID = "vimObjectId";
	public static final String NETWORK_HOSTID = "hostId";
	public static final String NETWORK_HOSTNAME = "hostName";
	public static final String NETWORK_VMVNICS = "vmVnics";
	public static final String NETWORK_VN = "vn";
	public static final String NETWORK_MACADDRESS = "macAddress";
	public static final String NETWORK_INTERNALIPADDRESS = "internalIpAddress";
	public static final String NETWORK_EXTERNALIPADDRESS = "externalIpAddress";
	public static final String NETWORK_VRFLIST = "vrflist";
	public static final String NETWORK_ISEXTERNALLYCONNECTED = "isExternallyConnected";
	public static final String NETWORK_INTERNALVNICS = "internalVnics";
	public static final String NETWORK_EXTERNALVNICS = "externalVnics";

	// /pm/{jobId}/notification
	public static final String PM_NOTIFICATION_CREATEAT = "CreateAt";
	public static final String PM_NOTIFICATION_KPIREPORT = "KPIReport";
	public static final String PM_NOTIFICATION_OBJECTID = "ObjectId";
	public static final String PM_NOTIFICATION_KPI = "KPI";
	public static final String PM_NOTIFICATION_METERITEM = "meterItem";
	public static final String PM_NOTIFICATION_NETERVALUE = "meterValue";
	public static final String PM_NOTIFICATION_METERUNIT = "meterUnit";
	public static final String PM_NOTIFICATION_OBJ_SECTOR_BOARDID = "boardid";
	
	// createPmJob
	public static final String PM_JOB_TYPE = "Type";
	public static final String PM_JOB_OBJECTID = "ObjectId";
	public static final String PM_JOB_KPI = "KPI";
	public static final String PM_JOB_PERIOD = "Period";
	public static final String PM_JOB_RESULT_LINKS = "Links";
	public static final String PM_JOB_RESULT_SELF = "Self";

	// QueryVnfAlarm
	public static final String FM_VNF_ALARMS = "alarms";
	public static final String FM_VNF_ALARMNAME = "alarmName";
	public static final String FM_VNF_SEVERITY = "Severity";
	public static final String FM_VNF_TYPE = "Type";
	public static final String FM_VNF_TIMESTAMP = "timeStamp";
	public static final String FM_VNF_COMPONENT = "Component";
	public static final String FM_VNF_BODY = "body";
	public static final String FM_VNF_PROBCAUSE = "probCause";

	// Common
	public static final String PARA_NFVOID = "NFVOID";
	public static final String PARA_VNFMID = "VNFMID";
	public static final String PARA_VIMID = "VIMID";

	// VM Alarm Trap
	public static final String OID_EVENT_TIME = "1.3.6.1.4.1.193.183.4.1.3.5.1.7.0";
	public static final String OID_SEQUENCE_NO = "1.3.6.1.4.1.193.183.4.1.3.3.0";
	public static final String OID_SEVERITY = "1.3.6.1.6.3.1.1.4.1.0";
	public static final String OID_SOURCE = "1.3.6.1.4.1.193.183.4.1.3.5.1.5.0";
	public static final String OID_MAJOR = "1.3.6.1.4.1.193.183.4.1.3.5.1.2.0";
	public static final String OID_MINOR = "1.3.6.1.4.1.193.183.4.1.3.5.1.3.0";
	public static final String OID_EVENT_TYPE = "1.3.6.1.4.1.193.183.4.1.3.5.1.6.0";
	public static final String OID_PROBABLE_CAUSE = "1.3.6.1.4.1.193.183.4.1.3.5.1.9.0";
	public static final String OID_SPECIFIC_PROBLEM = "1.3.6.1.4.1.193.183.4.1.3.5.1.4.0";
	public static final String OID_ADDITIONAL_TEXT = "1.3.6.1.4.1.193.183.4.1.2.1.0";
	public static final String OID_ADDITIONAL_INFO = "1.3.6.1.4.1.193.183.4.1.2.2.0";
}
