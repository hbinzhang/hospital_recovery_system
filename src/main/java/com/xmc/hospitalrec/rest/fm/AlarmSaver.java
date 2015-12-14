package com.xmc.hospitalrec.rest.fm;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.xmc.hospitalrec.model.CurAlarmVm;

@Component
public class AlarmSaver extends Thread {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlarmSaver.class);
	private static final BlockingQueue<CurAlarmVm> alarmQueue = new LinkedBlockingQueue<CurAlarmVm>();

	@Inject
	AlarmService alarmService;
	
	@Override
	public void run() {
		while(true) {
			try {
				LOGGER.debug("wait for alarm...");
				CurAlarmVm alarm = alarmQueue.take();
				LOGGER.debug("save alarm " + alarm);
				this.alarmService.saveAlarmVm(alarm);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void addAlarm(CurAlarmVm alarm) {
		alarmQueue.add(alarm);
	}
}
