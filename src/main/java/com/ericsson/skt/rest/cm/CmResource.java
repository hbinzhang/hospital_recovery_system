package com.ericsson.skt.rest.cm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
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
import com.ericsson.skt.dao.CmDao;
import com.ericsson.skt.model.Vm;
import com.ericsson.skt.model.VmVnics;

/*
 * By default, Jersey instantiates resource classes in a per-request basis (the default and the only way standardized in JAX-RS).
 * However, when @Component is used, that is, when these resource classes are managed by Spring context, it becomes singleton. You cannot
 * use @Scope("request") to make it request-scoped, because the request life-cycle is not managed by Spring (i.e., the front-dispatcher
 * is not Spring). @Singleton annotation (which signals Jersey to use a singleton instance) is added here merely to remind this fact.
 */
@Singleton
@Component
@Path("cm")
@Transactional(propagation = Propagation.REQUIRED)
@Produces(MediaType.APPLICATION_JSON)
public class CmResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(CmResource.class);

	@Inject
	private CmDao dao;

	@GET
	@Path("/topo")
	public Response getTopo() {
		LOGGER.info("[getTopo]");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		Map<String, Object> hostMap = null;
		List<Map<String, Object>> hostMapList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> vmMapList = null;
		Map<String, Object> vmMap = null;

		dataMap.put("vns", getVnNameMapList());

		List<String> hostNameList = this.dao.getHostNameList();
		for (String hostName : hostNameList) {
			hostMap = new HashMap<String, Object>();

			hostMap.put("hostname", hostName);
			vmMapList = new ArrayList<Map<String, Object>>();
			List<Vm> vmList = this.dao.getVmList(hostName);
			for (Vm vm : vmList) {
				vmMap = new HashMap<String, Object>();
				vmMap.put("vmname", vm.getVmName());
				vmMap.put("vmid", vm.getVmId());

				vmMap.put("belongto", this.getVmVnicsList(vm.getVmId()));

				vmMapList.add(vmMap);
			}
			hostMap.put("vms", vmMapList);

			hostMapList.add(hostMap);
		}

		dataMap.put("hosts", hostMapList);
		resultMap.put("data", dataMap);

		return Response.ok().entity(resultMap).build();
	}

	private List<Map<String, String>> getVnNameMapList() {
		List<String> vnNameList = this.dao.getVnNameList();
		List<Map<String, String>> vnNameMapList = new ArrayList<Map<String, String>>();
		for (String vnName : vnNameList) {
			Map<String, String> vnNameMap = new HashMap<String, String>();
			vnNameMap.put("vnname", vnName);
			vnNameMapList.add(vnNameMap);
		}
		return vnNameMapList;
	}

	private List<Map<String, String>> getVmVnicsList(String vmId) {
		List<Map<String, String>> vnNameMapList = new ArrayList<Map<String, String>>();
		List<VmVnics> vmVnicsList = this.dao.getVmVnicsList(vmId);
		for (VmVnics vmvnics : vmVnicsList) {
			Map<String, String> vmvnicsMap = new HashMap<String, String>();
			// vmvnicsMap.put("vnid", vmvnics.getVnId());
			vmvnicsMap.put("vnname", vmvnics.getVnName());
			vnNameMapList.add(vmvnicsMap);
		}
		return vnNameMapList;
	}

	@GET
	@Path("/config")
	public Response getConfig() {
		LOGGER.info("[getConfig]");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("vnfInstanceID", Configure.getVnfInstanceId());

		return Response.ok().entity(resultMap).build();
	}
}
