package com.ericsson.skt.rest.cm;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.ericsson.skt.rest.pm.PmService;

@Component
public class NetworkInitializer extends Thread {
	@Inject
	private CmService cmService;
	
	@Inject
	private PmService pmService;

	@Override
	public void run() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
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
	}
}
