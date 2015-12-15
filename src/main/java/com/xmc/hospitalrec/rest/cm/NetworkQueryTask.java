package com.xmc.hospitalrec.rest.cm;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xmc.hospitalrec.Configure;
import com.xmc.hospitalrec.rest.pm.PmService;

@Component
@Transactional(propagation = Propagation.REQUIRED)
public class NetworkQueryTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(NetworkQueryTask.class);
	private static int counter = 0;
	@Inject
	private CmService cmService;

	@Inject
	private PmService pmService;

//	@Scheduled(cron = "30 * * * * *")
	public void updateNetwork() {
		counter++;
		if (counter >= Configure.getTopoUpdatePeriod()) {
			LOGGER.info("[updateNetwork]");
			try {
				cmService.updateNetwork();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				pmService.createPmJob();
			} catch (Exception e) {
				e.printStackTrace();
			}
			counter = 0;
		}
	}

}
