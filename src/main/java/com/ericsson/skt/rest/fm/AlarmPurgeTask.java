package com.ericsson.skt.rest.fm;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Component
@Transactional(propagation = Propagation.REQUIRED)
public class AlarmPurgeTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlarmPurgeTask.class);

	@Inject
	private AlarmService service;

	@Scheduled(cron="45 0 0 * * *")
	public void deleteOvertimeAlarm() {
		LOGGER.info("[deleteOvertimeAlarm]");
		
		service.deleteOvertimeAlarm();
	}
}
