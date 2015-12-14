package com.ericsson.skt.rest.br;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ericsson.skt.Configure;
import com.ericsson.skt.model.BackupEvent;
import com.ericsson.skt.model.BackupTask;
import com.ericsson.skt.model.Message;
import com.ericsson.skt.rest.util.Util;
import com.ericsson.skt.ssh.SshTest;
import com.ericsson.skt.ssh.TimeConventor;

@Singleton
@Component
@Path("br")
@Transactional(propagation = Propagation.REQUIRED)
@Produces(MediaType.APPLICATION_JSON)
public class BrResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(BrResource.class);

	public static final String backupScriptName = "epg_backup_utils.py";
	
	@Inject
	BrService service;

	@GET
	@Path("/backup")
	public Response getBackupTask() {

		LOGGER.info("[getBackupTask]");
		List<BackupTask> backupTaskList = service.getBackupTask();

		return Response.ok().entity(Util.genPageQueryResult(backupTaskList.size(), backupTaskList)).build();
	}

	@POST
	@Path("/backup")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setBackupTask(BackupTask backupTask) {
		LOGGER.info("[setBackupTask] backupTask=" + backupTask);
		String interval = "";
		if (backupTask.getPeriod().equals("Every Day")) {
			interval = "24";
		} else if (backupTask.getPeriod().equals("Every Week")) {
			interval = "168";
		} else if (backupTask.getPeriod().equals("Every Month")) {
			interval = "5040";
		}
		service.setBackupTask(backupTask);
		Date triggerD = TimeConventor.stringTime2Date(backupTask.getTriggerTime(), TimeConventor.FORMAT_SLASH_24HOUR);
		String triggerTimeStr = TimeConventor.date2String(triggerD, TimeConventor.FORMAT_MINUS_24HOUR_VEPG);
		// run script
		// epg_backup_utils.py -n epgnode -u username -p password -i interval -t time
		SshTest sshUtil = new SshTest();
		sshUtil.connect(Configure.getConfigValue(Configure.VEPG_HOST_IP), 22, 
				Configure.getConfigValue(Configure.VEPG_HOST_USERNAME), 
				Configure.getConfigValue(Configure.VEPG_HOST_PASSWORD));
		String cmd = backupScriptName + 
				" -n " + Configure.getVnfInstanceId() + 
				" -u " + Configure.getConfigValue(Configure.VEPG_HOST_TUN_USERNAME) + 
				" -p " + Configure.getConfigValue(Configure.VEPG_HOST_TUN_PASSWORD) + 
				" -i " + interval + 
				" -t " + triggerTimeStr;
		String ret = sshUtil.execCmd(cmd);
		if (ret.equals("0")) {
			LOGGER.info("[setBackupTask] operate VEPG succ");
		} else {
			LOGGER.info("[setBackupTask] operate VEPG failed");
		}
		sshUtil.disconnect();
		return Response.created(null).entity(backupTask).build();
	}

	@GET
	@Path("/backupevents")
	public Response getBackupEvent() {
		LOGGER.info("[getBackupEvent]");
		// epg_backup_utils.py -n epgnode 每u username 每p password -l outputfile
		SshTest sshUtil = new SshTest();
		sshUtil.connect(Configure.getConfigValue(Configure.VEPG_HOST_IP), 22, 
				Configure.getConfigValue(Configure.VEPG_HOST_USERNAME), 
				Configure.getConfigValue(Configure.VEPG_HOST_PASSWORD));
		String cmd = backupScriptName + 
				" -n " + Configure.getVnfInstanceId() + 
				" -u " + Configure.getConfigValue(Configure.VEPG_HOST_TUN_USERNAME) + 
				" -p " + Configure.getConfigValue(Configure.VEPG_HOST_TUN_PASSWORD) + 
				" -l " + Configure.VEPG_HOST_OUTPUT_FILE_NAME;
		String ret = sshUtil.execCmd(cmd);
		List<BackupEvent> retLst = new ArrayList<BackupEvent>();
		// get file content
		String getFileCmd = "cat " + Configure.VEPG_HOST_OUTPUT_FILE_NAME;
		ret = sshUtil.execCmd(getFileCmd);
		if (ret != null && ret.length() > 0) {
			String[] rs = ret.split("\n");
			for (String r : rs) {
				String[] secs = r.split(" ");
				BackupEvent e = new BackupEvent();
				retLst.add(e);
				e.setTime(secs[0]);
				e.setEvent(secs[1]);
				e.setDump(secs[2]);
				e.setStatus(secs[3]);
			}
		}
		LOGGER.info("[getBackupEvent] ret " + retLst);
		String rmFileCmd = "rm -rf " + Configure.VEPG_HOST_OUTPUT_FILE_NAME;
		ret = sshUtil.execCmd(rmFileCmd);
		LOGGER.info("[getBackupEvent] delete file OK");
		sshUtil.disconnect();
		return Response.ok().entity(Util.genPageQueryResult(retLst.size(), retLst)).build();
	}
	
	@GET
	@Path("/backfiles")
	public Response getBackupFileName() {
		LOGGER.info("[getBackupFileName]");
		// epg_backup_utils.py -n epgnode 每u username 每p password -b
		SshTest sshUtil = new SshTest();
		sshUtil.connect(Configure.getConfigValue(Configure.VEPG_HOST_IP), 22, 
				Configure.getConfigValue(Configure.VEPG_HOST_USERNAME), 
				Configure.getConfigValue(Configure.VEPG_HOST_PASSWORD));
		String cmd = backupScriptName + 
				" -n " + Configure.getVnfInstanceId() + 
				" -u " + Configure.getConfigValue(Configure.VEPG_HOST_TUN_USERNAME) + 
				" -p " + Configure.getConfigValue(Configure.VEPG_HOST_TUN_PASSWORD) + 
				" -b";
		String ret = sshUtil.execCmd(cmd);
		List<Message> retLst = new ArrayList<Message>();
		if (ret != null && ret.length() > 0) {
			String[] rets = ret.split("\n");
			for (String r : rets) {
				retLst.add(new Message(r));
			}
		}
		sshUtil.disconnect();
		return Response.ok().entity(Util.genPageQueryResult(retLst.size(), retLst)).build();
	}
	
	@SuppressWarnings("rawtypes")
	@POST
	@Path("/restore")
	public Response doRestore(Map obj) {
		LOGGER.info("[getBackupFileName] para: " + obj);
		//epg_backup_utils.py -n epgnode -u username -p password -r backupfile
		SshTest sshUtil = new SshTest();
		sshUtil.connect(Configure.getConfigValue(Configure.VEPG_HOST_IP), 22, 
				Configure.getConfigValue(Configure.VEPG_HOST_USERNAME), 
				Configure.getConfigValue(Configure.VEPG_HOST_PASSWORD));
		String cmd = backupScriptName + 
				" -n " + Configure.getVnfInstanceId() + 
				" -u " + Configure.getConfigValue(Configure.VEPG_HOST_TUN_USERNAME) + 
				" -p " + Configure.getConfigValue(Configure.VEPG_HOST_TUN_PASSWORD) + 
				" -r " + obj.get("filename");
		String ret = sshUtil.execCmd(cmd);
		if (ret.equals("0")) {
			LOGGER.info("[doRestore] operate VEPG succ");
		} else {
			LOGGER.info("[doRestore] operate VEPG failed");
		}
		sshUtil.disconnect();
		return Response.ok().entity(new Message("Exec restore result: " + ret)).build();
	}
}
