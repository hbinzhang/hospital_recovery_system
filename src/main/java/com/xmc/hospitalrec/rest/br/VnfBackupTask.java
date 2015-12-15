package com.xmc.hospitalrec.rest.br;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xmc.hospitalrec.model.BackupTask;

@Component
@Transactional(propagation = Propagation.REQUIRED)
public class VnfBackupTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(VnfBackupTask.class);

	@Inject
	BrService service;

//	@Scheduled(cron = "0 * * * * *")
	public void backup() {
		LOGGER.info("[backup]");

		List<BackupTask> taskList = service.getBackupTask();
		for (BackupTask task : taskList) {
			doBackup(task, new Date());
		}
	}

	private void doBackup(BackupTask task, Date now) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		GregorianCalendar gcTrigger = new GregorianCalendar();
		GregorianCalendar gcNow = new GregorianCalendar();
		boolean bExecFlag = false;

		try {
			gcTrigger.setTime(sf.parse(task.getTriggerTime()));
			gcNow.setTime(now);

			switch (task.getPeriod()) {
			case BackupTask.PERIOD_DAY:
				if (gcTrigger.get(Calendar.HOUR_OF_DAY) == gcNow.get(Calendar.HOUR_OF_DAY)
						&& gcTrigger.get(Calendar.MINUTE) == gcNow.get(Calendar.MINUTE))
					bExecFlag = true;
				break;
			case BackupTask.PERIOD_WEEK:
				if (gcTrigger.get(Calendar.HOUR_OF_DAY) == gcNow.get(Calendar.HOUR_OF_DAY)
						&& gcTrigger.get(Calendar.MINUTE) == gcNow.get(Calendar.MINUTE)
						&& gcTrigger.get(Calendar.DAY_OF_WEEK) == gcNow.get(Calendar.DAY_OF_WEEK))
					bExecFlag = true;
				break;
			case BackupTask.PERIOD_MONTH:
				if (gcTrigger.get(Calendar.HOUR_OF_DAY) == gcNow.get(Calendar.HOUR_OF_DAY)
						&& gcTrigger.get(Calendar.MINUTE) == gcNow.get(Calendar.MINUTE)
						&& gcTrigger.get(Calendar.DAY_OF_MONTH) == gcNow.get(Calendar.DAY_OF_MONTH))
					bExecFlag = true;
				break;
			}

			if (!bExecFlag)
				return;

			LOGGER.debug("[doBackup] @@@@@@@ " + task);

		} catch (ParseException e) {
			LOGGER.error("[doBackup] triggerTime invalid.", e);
		}
	}
}
