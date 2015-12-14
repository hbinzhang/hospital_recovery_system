package com.ericsson.skt.rest.pm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ericsson.skt.Configure;
import com.ericsson.skt.dao.PmDao;
import com.ericsson.skt.model.PmJob;
import com.ericsson.skt.rest.util.InterfaceConsts;
import com.ericsson.skt.rest.util.Util;

@Component
@Transactional(propagation = Propagation.REQUIRED)
public class PmService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PmService.class);

	@Inject
	private PmDao dao;

	public void createPmJob() {
		LOGGER.info("[createPmJob]");

		invokeDeletePmJob(false);
		invokeDeletePmJob(true);

		PmJob pmJob = invokeCreatePmJob("VM");
		this.dao.insert(pmJob);

		pmJob = invokeCreatePmJob("HOST");
		this.dao.insert(pmJob);

		pmJob = invokeCreatePmJob("VNF");
		this.dao.insert(pmJob);
	}

	@SuppressWarnings("unchecked")
	private PmJob invokeCreatePmJob(String type) {
		PmJob pmJob = null;
		WebTarget target = null;
		List<String> kpiList = new ArrayList<String>();
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		List<String> objectList = new ArrayList<String>();

		jsonMap.put(InterfaceConsts.PARA_NFVOID, Configure.getNfvoId());
		jsonMap.put(InterfaceConsts.PM_JOB_KPI, kpiList);
		jsonMap.put(InterfaceConsts.PM_JOB_PERIOD, Configure.getPmJobPeriod());
		jsonMap.put(InterfaceConsts.PM_JOB_TYPE, type);

		switch (type) {
		case "VM":
			kpiList.add("network.outgoing.bytes");
			kpiList.add("network.incoming.bytes");
			kpiList.add("CPU_UTIL");
			jsonMap.put(InterfaceConsts.PARA_VIMID, Configure.getVimId());
			jsonMap.put(InterfaceConsts.PM_JOB_OBJECTID, this.dao.getVmIdList());
			target = Util.getWebTarget("/v1/pm/jobs", Configure.getVimgwUrl());
			break;
		case "HOST":
			kpiList.add("MEM_UTIL");
			kpiList.add("CPU_UTIL");
			jsonMap.put(InterfaceConsts.PARA_VIMID, Configure.getVimId());
			jsonMap.put(InterfaceConsts.PM_JOB_OBJECTID, this.dao.getHostNameList());
			target = Util.getWebTarget("/v1/pm/jobs", Configure.getVimgwUrl());
			break;
		case "VNF":
			kpiList.add("averageCpuUsage");
			kpiList.add("memoryUsed");
			kpiList.add("memory");
			kpiList.add("ggsnDownlinkBytes");
			kpiList.add("ggsnUplinkBytes");
			objectList.add(Configure.getVnfInstanceId());
			jsonMap.put(InterfaceConsts.PM_JOB_OBJECTID, objectList);
			jsonMap.put(InterfaceConsts.PARA_VIMID, Configure.getRcgwId());
			target = Util.getWebTarget("/v1/pm/jobs", Configure.getRcgwUrl());
			break;
		}

		LOGGER.debug("[invokeCreatePmJob] target=" + target.toString() + ", parameter map=" + jsonMap);
		Response response = target.request().buildPost(Entity.entity(jsonMap, MediaType.APPLICATION_JSON)).invoke();
		int status = response.getStatus();
		LOGGER.debug("[invokeCreatePmJob] return status=" + status + ", 201,200 is expected");
		if (status == Status.CREATED.getStatusCode() || status == Status.OK.getStatusCode()) {
			Map<String, Object> resultMap = response.readEntity(Map.class);
			LOGGER.debug("[invokeCreatePmJob] entity=" + resultMap.toString());

			List<Map<String, String>> linkList = (List<Map<String, String>>) resultMap
					.get(InterfaceConsts.PM_JOB_RESULT_LINKS);
			LOGGER.debug("[invokeCreatePmJob] linkList= " + linkList);
			String jobId = null;
			for (Map<String, String> map : linkList) {
				LOGGER.debug("[invokeCreatePmJob] map= " + map);
				String rel = map.get(InterfaceConsts.PM_JOB_RESULT_SELF);
				LOGGER.debug("[invokeCreatePmJob] rel= " + rel);
				if (rel != null) {
					int index = rel.lastIndexOf("/");
					jobId = rel.substring(index + 1, rel.length());
					LOGGER.debug("[invokeCreatePmJob] jobId= " + jobId);
					break;
				}
			}
			if (jobId != null) {
				pmJob = new PmJob();
				LOGGER.debug("[invokeCreatePmJob] create job= " + jobId);
				pmJob.setJobId(jobId);
				pmJob.setKpi(kpiList.toString());
				pmJob.setNfvoId(Configure.getNfvoId());
				pmJob.setVimId(Configure.getVimId());
				pmJob.setPeriod(Configure.getPmJobPeriod());
				pmJob.setObjecteId("[]");
				pmJob.setType(type);
			}
		}
		response.close();
		LOGGER.debug("[invokeCreatePmJob] return job= " + pmJob);
		return pmJob;
	}

	private void invokeDeletePmJob(boolean vnfFlag) {
		WebTarget target = null;
		List<String> jobIdList = this.dao.queryPmJobList(vnfFlag);
		for (String jobId : jobIdList) {
			if (vnfFlag) {
				target = Util.getWebTarget("/v1/pm/jobs/" + jobId, Configure.getRcgwUrl());
			} else {
				target = Util.getWebTarget("/v1/pm/jobs/" + jobId, Configure.getVimgwUrl());
			}
			LOGGER.debug("[invokeDeletePmJob] target=" + target.toString());
			Response response = target.request().delete();
			int status = response.getStatus();
			response.close();
			LOGGER.debug("[invokeDeletePmJob] return status=" + status + ",204 and 404 is expected");
			if (status == Status.NO_CONTENT.getStatusCode() || status == Status.NOT_FOUND.getStatusCode()) {
				Map<String, Object> conditionMap = new HashMap<String, Object>();

				conditionMap.put("jobId", jobId);
				this.dao.delete(PmJob.class, conditionMap);
			}
		}
	}
}
