package com.ericsson.skt.rest.upgrade;

import java.util.List;

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
import com.ericsson.skt.dao.BrDao;
import com.ericsson.skt.model.Message;
import com.ericsson.skt.model.UpTask;
import com.ericsson.skt.rest.util.Util;
import com.ericsson.skt.ssh.SshTest;

@Singleton
@Component
@Path("up")
@Transactional(propagation = Propagation.REQUIRED)
@Produces(MediaType.APPLICATION_JSON)
public class UpResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(UpResource.class);

	public static final String upgradeScriptName = "epg_upgrade_utils.py";
	
	@Inject
	BrDao dao;
	
	@SuppressWarnings("unchecked")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/setUpTask")
	public Response setUpTask(UpTask task) {
		LOGGER.info("[setUpTask] task is: " + task);
		List<UpTask> all = (List<UpTask>)dao.get(UpTask.class);
		if (all != null && all.size() > 0) {
			dao.delete(all.get(0));
		}
		dao.insert(task);
		LOGGER.info("[setUpTask] insert task");
		// operate device
	 	// epg_upgrade_utils.py -n epgnode -u username -p password -t time -b upgradepkg -d delay -z usernum
		SshTest sshUtil = new SshTest();
		sshUtil.connect(Configure.getConfigValue(Configure.VEPG_HOST_IP), 22, 
				Configure.getConfigValue(Configure.VEPG_HOST_USERNAME), 
				Configure.getConfigValue(Configure.VEPG_HOST_PASSWORD));
		String cmd = upgradeScriptName + 
				" -n " + Configure.getVnfInstanceId() + 
				" -u " + Configure.getConfigValue(Configure.VEPG_HOST_TUN_USERNAME) + 
				" -p " + Configure.getConfigValue(Configure.VEPG_HOST_TUN_PASSWORD) + 
				" -b " + task.getSwName() + 
				" -d " + Integer.parseInt(task.getTimeConsumed()) * 60 + 
				" -z " + task.getUserRemain();
		sshUtil.execCmd(cmd);
		LOGGER.info("[setUpTask] exec ok");
		sshUtil.disconnect();
		return Response.ok().entity(task).build();
	}

	@SuppressWarnings("rawtypes")
	@GET
	@Path("/getUpTask")
	public Response getUpTask() {
		LOGGER.info("[getUpTask]");
		List r = dao.get(UpTask.class);
		LOGGER.info("[getUpTask] ret " + r);
		return Response.ok().entity(Util.genPageQueryResult(r.size(), r)).build();
	}

	@SuppressWarnings("unchecked")
	@POST
	@Path("/cancelUpTask")
	public Response cancelUpTask() {
		LOGGER.info("[cancelUpTask]");
		// epg_upgrade_utils.py -n epgnode -u username -p password ¨Cc
		SshTest sshUtil = new SshTest();
		sshUtil.connect(Configure.getConfigValue(Configure.VEPG_HOST_IP), 22, 
				Configure.getConfigValue(Configure.VEPG_HOST_USERNAME), 
				Configure.getConfigValue(Configure.VEPG_HOST_PASSWORD));
		List<UpTask> all = (List<UpTask>)dao.get(UpTask.class);
		if (all != null && all.size() > 0) {
			dao.delete(all.get(0));
		}
		String cmd = upgradeScriptName + 
				" -n " + Configure.getVnfInstanceId() + 
				" -u " + Configure.getConfigValue(Configure.VEPG_HOST_TUN_USERNAME) + 
				" -p " + Configure.getConfigValue(Configure.VEPG_HOST_TUN_PASSWORD) + 
				" -c";
		String ret = sshUtil.execCmd(cmd);
		if (ret == null) {
			return Response.ok().entity(new Message("Exec cancel up task result: " + ret)).build();
		}
		if (ret.equals("0")) {
			LOGGER.info("[cancelUpTask] operate VEPG succ");
		} else {
			LOGGER.info("[cancelUpTask] operate VEPG failed");
		}
		LOGGER.info("[cancelUpTask] cancel OK");
		sshUtil.disconnect();
		return Response.ok().entity(new Message("Exec cancel up task result: " + ret)).build();
	}
	
	@GET
	@Path("/getUserNum")
	public Response getUserNum() {
		LOGGER.info("[getUserNum]");
		// epg_upgrade_utils.py -n epgnode ¨Cu username ¨Cp password -q -j jobid
		SshTest sshUtil = new SshTest();
		sshUtil.connect(Configure.getConfigValue(Configure.VEPG_HOST_IP), 22, 
				Configure.getConfigValue(Configure.VEPG_HOST_USERNAME), 
				Configure.getConfigValue(Configure.VEPG_HOST_PASSWORD));
		String cmd = upgradeScriptName + 
				" -n " + Configure.getVnfInstanceId() + 
				" -u " + Configure.getConfigValue(Configure.VEPG_HOST_TUN_USERNAME) + 
				" -p " + Configure.getConfigValue(Configure.VEPG_HOST_TUN_PASSWORD) + 
				" -q "; 
				//" -j " + ;//-j: job id. It¡¯s an optional flag.
		String ret = sshUtil.execCmd(cmd);
		String retNum = "36";
		// User number: 36
		if (ret != null && ret.length() > 0) {
			String[] rets = ret.split("\n");
			for (String r : rets) {
				if (r.contains("User number") ) {
					retNum = r.replace("User number", "");
					retNum = retNum.replace(":", "");
					retNum = retNum.trim();
				}
			}
		}
		sshUtil.disconnect();
		return Response.ok().entity(retNum).build();
	}
	
	/**
	 * Not used now!
	 * @return
	 */
	@POST
	@Path("/fetchUpLog")
	public Response fetchUpLog() {
		LOGGER.info("[fetchUpLog]");
		//  	epg_upgrade_utils.py -n epgnode -u username -p password -l outputfile
		SshTest sshUtil = new SshTest();
		sshUtil.connect(Configure.getConfigValue(Configure.VEPG_HOST_IP), 22, 
				Configure.getConfigValue(Configure.VEPG_HOST_USERNAME), 
				Configure.getConfigValue(Configure.VEPG_HOST_PASSWORD));
		String cmd = upgradeScriptName + 
				" -n " + Configure.getVnfInstanceId() + 
				" -u " + Configure.getConfigValue(Configure.VEPG_HOST_TUN_USERNAME) + 
				" -p " + Configure.getConfigValue(Configure.VEPG_HOST_TUN_PASSWORD) + 
				" -l " + Configure.VEPG_HOST_OUTPUT_FILE_NAME;
		String ret = sshUtil.execCmd(cmd);
		// GUI model not specified
		sshUtil.disconnect();
		return Response.ok().entity(new Message("Exec fetchUpLog OK: " + ret)).build();
	}
}
