package com.ericsson.skt;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.ericsson.skt.rest.cm.NetworkInitializer;
import com.ericsson.skt.rest.fm.AlarmSaver;
import com.ericsson.skt.rest.fm.TrapReceiver;

public class ApplicationContextInitializer implements ApplicationListener<ContextRefreshedEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContextInitializer.class);

	@Inject
	private TrapReceiver trapReceiver;

	@Inject
	private AlarmSaver alarmSaver;

	@Inject
	private NetworkInitializer networkInitializer;

	@Override
	public void onApplicationEvent(final ContextRefreshedEvent event) {
		LOGGER.info("TrapReceiver started.");
		trapReceiver.run();

		LOGGER.info("AlarmSaver Thread started.");
		alarmSaver.start();

		LOGGER.info("NetworkInitializer thread started.");
		networkInitializer.start();
	}
}
