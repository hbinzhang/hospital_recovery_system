package com.ericsson.skt.rest.br;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ericsson.skt.dao.BrDao;
import com.ericsson.skt.model.BackupEvent;
import com.ericsson.skt.model.BackupTask;

@Component
@Transactional(propagation = Propagation.REQUIRED)
public class BrService {
	//private static final Logger LOGGER = LoggerFactory.getLogger(BrService.class);

	@Inject
	BrDao dao;

	public void setBackupTask(BackupTask backupTask) {
		this.dao.setBackupTask(backupTask);
	}
	
	@SuppressWarnings("unchecked")
	public List<BackupTask> getBackupTask() {
		return (List<BackupTask>)this.dao.get(BackupTask.class);
	}

	@SuppressWarnings("unchecked")
	public List<BackupEvent> getBackupEvent() {
		return (List<BackupEvent>)this.dao.get(BackupEvent.class);
	}
}
