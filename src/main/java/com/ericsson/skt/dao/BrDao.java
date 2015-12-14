package com.ericsson.skt.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ericsson.skt.model.BackupTask;

@Repository("brDao")
@Transactional(propagation = Propagation.SUPPORTS)
public class BrDao extends AbstractDao {

	public void setBackupTask(BackupTask backupTask) {
		BackupTask task = (BackupTask) this.find(BackupTask.class, backupTask.getVnfName());
		if(task==null) {
			this.insert(backupTask);
		} else {
			this.update(backupTask);
		}
	}

}
