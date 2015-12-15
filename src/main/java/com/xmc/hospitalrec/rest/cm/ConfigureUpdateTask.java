package com.xmc.hospitalrec.rest.cm;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.xmc.hospitalrec.Configure;

@Component
public class ConfigureUpdateTask {

//	@Scheduled(cron = "0 0/10 * * * *")
	public void updateConfig() {
		Configure.reload();
	}

}
