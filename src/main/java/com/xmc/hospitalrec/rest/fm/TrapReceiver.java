package com.xmc.hospitalrec.rest.fm;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;
import org.springframework.stereotype.Component;

import com.xmc.hospitalrec.model.CurAlarmVm;
import com.xmc.hospitalrec.rest.util.InterfaceConsts;

@Singleton
@Component
public class TrapReceiver implements CommandResponder {
	private static final Logger LOGGER = LoggerFactory.getLogger(TrapReceiver.class);
	private static final Map<String, String> alarmSeverityMap = new HashMap<String, String>();
	private static final Map<String, String> alarmTypeMap = new HashMap<String, String>();
	private static final Map<String, String> alarmCauseMap = new HashMap<String, String>();
	private MultiThreadedMessageDispatcher dispatcher;
	private Snmp snmp = null;
	private Address listenAddress;
	private ThreadPool threadPool;

	static {
		// alarmSeverityMap.put("2031675", "MAJOR");
		// alarmSeverityMap.put("2031702", "MINOR");
		// alarmSeverityMap.put("2031678", "MINOR");
		// alarmSeverityMap.put("2031703", "WARNING");

		alarmSeverityMap.put("1.3.6.1.4.1.193.183.4.2.0.1", "Indeterminate");
		alarmSeverityMap.put("1.3.6.1.4.1.193.183.4.2.0.2", "Warning");
		alarmSeverityMap.put("1.3.6.1.4.1.193.183.4.2.0.3", "Minor");
		alarmSeverityMap.put("1.3.6.1.4.1.193.183.4.2.0.4", "Major");
		alarmSeverityMap.put("1.3.6.1.4.1.193.183.4.2.0.5", "Critical");
		alarmSeverityMap.put("1.3.6.1.4.1.193.183.4.2.0.7", "Clear");

		alarmTypeMap.put("1", "other");
		alarmTypeMap.put("2", "communicationsAlarm");
		alarmTypeMap.put("5", "equipmentAlarm");
		
		alarmCauseMap.put("0", "m3100Indeterminate");
		alarmCauseMap.put("77", "lossOfRedundancy");
		alarmCauseMap.put("69", "m3100ReplaceableUnitProblem");
		alarmCauseMap.put("8", "m3100LossOfSignal");
		alarmCauseMap.put("100523", "lanError");
		alarmCauseMap.put("107", "m3100CoolingFanFailure");
		alarmCauseMap.put("14", "m3100Unavailable");
		alarmCauseMap.put("165", "underlayingResourceUnavailable");
		alarmCauseMap.put("100541", "resourceAtOrNearingCapacity");
		alarmCauseMap.put("78", "powerSupplyFailure");
		alarmCauseMap.put("207", "systemResourcesOverload");
	}

	public TrapReceiver() {
	}

	private void init() throws UnknownHostException, IOException {
		threadPool = ThreadPool.create("Trap", 2);
		dispatcher = new MultiThreadedMessageDispatcher(threadPool, new MessageDispatcherImpl());
		listenAddress = GenericAddress.parse(System.getProperty("snmp4j.listenAddress", "udp:0.0.0.0/162"));
		TransportMapping transport;
		if (listenAddress instanceof UdpAddress) {
			transport = new DefaultUdpTransportMapping((UdpAddress) listenAddress);
		} else {
			transport = new DefaultTcpTransportMapping((TcpAddress) listenAddress);
		}
		snmp = new Snmp(dispatcher, transport);
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());
		USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
		SecurityModels.getInstance().addSecurityModel(usm);
		snmp.listen();
	}

	public void run() {
		try {
			init();
			snmp.addCommandResponder(this);
		} catch (Exception ex) {
			LOGGER.error("init error", ex);
		}
	}

	public void processPdu(CommandResponderEvent respEvnt) {
		if (respEvnt == null || respEvnt.getPDU() == null)
			return;

		CurAlarmVm alarm = convertAlarm(respEvnt);
		LOGGER.debug("[processPdu] convert to alarm = " + alarm);

		if (alarm != null)
			AlarmSaver.addAlarm(alarm);
	}

	@SuppressWarnings("unchecked")
	private CurAlarmVm convertAlarm(CommandResponderEvent respEvnt) {
		int oidCount = 0;
		CurAlarmVm alarm = new CurAlarmVm();
		Vector<VariableBinding> recVBs = (Vector<VariableBinding>) respEvnt.getPDU().getVariableBindings();
		for (int i = 0; i < recVBs.size(); i++) {
			VariableBinding recVB = recVBs.elementAt(i);
			String oid = recVB.getOid().toString();
			String value = recVB.getVariable().toString();

			LOGGER.debug("[trap] " + oid + " : " + value);

			switch (oid) {
			case InterfaceConsts.OID_EVENT_TIME:
				alarm.setTimestamp(this.parseDateAndTime(value));
				oidCount++;
				break;
			case InterfaceConsts.OID_ADDITIONAL_INFO:
				alarm.setAdditionalInfo(value);
				oidCount++;
				break;
			case InterfaceConsts.OID_ADDITIONAL_TEXT:
				alarm.setAdditionalText(value);
				oidCount++;
				break;
			case InterfaceConsts.OID_EVENT_TYPE:
				alarm.setType(getAlarmType(value));
				oidCount++;
				break;
			case InterfaceConsts.OID_MAJOR:
				alarm.setMajor(value);
				oidCount++;
				break;
			case InterfaceConsts.OID_MINOR:
				alarm.setMinor(value);
				oidCount++;
				break;
			case InterfaceConsts.OID_PROBABLE_CAUSE:
				alarm.setProbCause(getAlarmCause(value));
				oidCount++;
				break;
			case InterfaceConsts.OID_SEQUENCE_NO:
				alarm.setSequenceNo(Long.parseLong(value));
				oidCount++;
				break;
			case InterfaceConsts.OID_SEVERITY:
				alarm.setSeverity(getAlarmSeverity(value));
				oidCount++;
				break;
			case InterfaceConsts.OID_SOURCE:
				oidCount++;
				alarm.setComponent(value);
				if (value != null) {
					int begin = value.indexOf(",VM=");
					if (begin > 0) {
						alarm.setVmId(value.substring(begin + 4, value.length()));
					} else {
						alarm.setVmId("");
					}
				}
				break;
			case InterfaceConsts.OID_SPECIFIC_PROBLEM:
				alarm.setSpecificProblem(value);
				oidCount++;
				break;
			}
		}

		return (oidCount == 11) ? alarm : null;
	}

	private String getAlarmSeverity(String minorType) {
		String severity = alarmSeverityMap.get(minorType);
		return (severity == null) ? minorType : severity;
	}

	private String getAlarmType(String typeId) {
		String type = alarmTypeMap.get(typeId);
		return (type == null) ? typeId : type;
	}

	private String getAlarmCause(String causeId) {
		String cause = alarmCauseMap.get(causeId);
		return (cause == null) ? causeId : cause;
	}

	private String parseDateAndTime(String strDateAndTime) {
		String[] sArray = strDateAndTime.split(":");
		int year, month, day, hour, minute, second;

		if (sArray.length != 11)
			return null;

		year = Integer.parseInt(sArray[0], 16) * 256 + Integer.parseInt(sArray[1], 16);
		month = Integer.parseInt(sArray[2], 16);
		day = Integer.parseInt(sArray[3], 16);
		hour = Integer.parseInt(sArray[4], 16) + 8;
		minute = Integer.parseInt(sArray[5], 16);
		second = Integer.parseInt(sArray[6], 16);

		return String.format("%04d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, minute, second);
	}

//	public static void main(String[] args) {
//		String s = "df";
//
//		int i = Integer.parseInt(s, 16);
//		System.out.println(i);
//
//		TrapReceiver tr = new TrapReceiver();
//		System.out.println(tr.parseDateAndTime("07:df:0a:1a:02:2d:03:00:2b:00:00"));
//	}

}
