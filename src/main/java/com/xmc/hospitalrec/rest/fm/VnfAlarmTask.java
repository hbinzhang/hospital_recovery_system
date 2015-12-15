package com.xmc.hospitalrec.rest.fm;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xmc.hospitalrec.rest.cm.NetworkQueryTask;

@Component
@Transactional(propagation = Propagation.REQUIRED)
public class VnfAlarmTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(NetworkQueryTask.class);
	
	@Inject
	private AlarmService service;
	
//	@Scheduled(cron="25 * * * * *")
	public void updateCurAlarmVnf() {
		LOGGER.info("[updateCurAlarmVnf]");
		
		service.saveAlarmVnf();
	}

}
