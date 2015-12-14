package com.xmc.hospitalrec.rest.fm;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xmc.hospitalrec.Configure;
import com.xmc.hospitalrec.dao.FmDao;
import com.xmc.hospitalrec.model.CurAlarmVm;
import com.xmc.hospitalrec.model.CurAlarmVnf;
import com.xmc.hospitalrec.rest.util.InterfaceConsts;
import com.xmc.hospitalrec.rest.util.Util;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class AlarmService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlarmService.class);
	@Inject
	private FmDao dao;

	public void saveAlarmVm(CurAlarmVm alarm) {
		if(alarm.getSeverity().equals("Clear")) {
			this.dao.clearVmAlarm(alarm);
		} else {
			alarm.setSequenceNo(0);
			this.dao.insert(alarm);
		}
	}

	public void saveAlarmVnf() {
		Map<String, Object> map = this.invokeQueryVnfAlarm(Configure.getVnfInstanceId(), Configure.getRcgwUrl());
		LOGGER.debug("[saveAlarmVnf] Configure.getRcgwUrl is= " + Configure.getRcgwUrl());
		if (map == null) {
			this.dao.saveCurAlarmVnf(null);
			return;
		}
		
		List<CurAlarmVnf> alarmVnfList = new ArrayList<CurAlarmVnf>();
		this.fetchCurAlarmVnf(map, alarmVnfList);
		this.dao.saveCurAlarmVnf(alarmVnfList);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> invokeQueryVnfAlarm(String vnfInstanceID, String rcgwUrl) {
		Map<String, Object> map = null;
		WebTarget target = Util.getWebTarget("/v1/VnfAlarm/" + vnfInstanceID, rcgwUrl);
		LOGGER.debug("[invokeQueryVnfAlarm] target=" + target.toString());

		Response response = target.request().get();
		int status = response.getStatus();
		LOGGER.debug("[invokeQueryVnfAlarm] return status=" + status + ", 200 is expected");

		if (status == Status.OK.getStatusCode()) {
			map = response.readEntity(Map.class);
			LOGGER.debug("[invokeQueryVnfAlarm] return entity=" + map);
		}
		response.close();
		return map;
	}

	@SuppressWarnings("unchecked")
	private void fetchCurAlarmVnf(Map<String, Object> map, List<CurAlarmVnf> alarmVnfList) {
		List<Map<String, Object>> alarmMapList = (List<Map<String, Object>>) map.get(InterfaceConsts.FM_VNF_ALARMS);
		for (Map<String, Object> alarmMap : alarmMapList) {
			CurAlarmVnf alarm = new CurAlarmVnf();
			
			alarm.setAlarmName((String)alarmMap.get(InterfaceConsts.FM_VNF_ALARMNAME));
			alarm.setAlarmBody((String)alarmMap.get(InterfaceConsts.FM_VNF_BODY));
			alarm.setComponent((String)alarmMap.get(InterfaceConsts.FM_VNF_COMPONENT));
			alarm.setVnfId(Configure.getVnfInstanceId());
			alarm.setProbCause((String)alarmMap.get(InterfaceConsts.FM_VNF_PROBCAUSE));
			alarm.setSeverity((String)alarmMap.get(InterfaceConsts.FM_VNF_SEVERITY));
			alarm.setTimestamp((String)alarmMap.get(InterfaceConsts.FM_VNF_TIMESTAMP));
			alarm.setType((String)alarmMap.get(InterfaceConsts.FM_VNF_TYPE));
			
			alarmVnfList.add(alarm);
		}
	}
	
	public void deleteOvertimeAlarm() {
		int days = -1 * Configure.getAlarmRemainDays();
		SimpleDateFormat sf  =new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		GregorianCalendar gc=new GregorianCalendar();
		gc.setTime(new Date());
		gc.add(Calendar.DAY_OF_YEAR ,days);
		
		String alarmTime = sf.format(gc.getTime());
		LOGGER.debug("[deleteOvertimeAlarm] alarmTime <= " + alarmTime + " will be deleted.");
		
		this.dao.deleteOvertimeAlarmVm(alarmTime);
		this.dao.deleteOvertimeAlarmVnf(alarmTime);
	}
}

