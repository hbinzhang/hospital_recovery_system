package com.xmc.hospitalrec.rest.simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xmc.hospitalrec.rest.util.InterfaceConsts;

/*
 * By default, Jersey instantiates resource classes in a per-request basis (the default and the only way standardized in JAX-RS).
 * However, when @Component is used, that is, when these resource classes are managed by Spring context, it becomes singleton. You cannot
 * use @Scope("request") to make it request-scoped, because the request life-cycle is not managed by Spring (i.e., the front-dispatcher
 * is not Spring). @Singleton annotation (which signals Jersey to use a singleton instance) is added here merely to remind this fact.
 */
@Singleton
@Component
@Path("/v1")
@Transactional(propagation = Propagation.REQUIRED)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SimulatorResource {
	@GET
	@Path("/VNFnetwork/{vnfInstanceID}")
	public String QueryVNFnetwork(@PathParam("vnfInstanceID") String vnfInstanceID) {
		System.out.println("$$$$$$$ QueryVNFnetwork called $$$$$$$$");
		System.out.println("vnfInstanceID=" + vnfInstanceID);

		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();

		try {
			reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(new File("c:/QueryVNFnetwork.txt")), "UTF-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append(System.getProperty("line.separator"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	@POST
	@Path("/pm/jobs")
	public Response createPmJob(String jsonMap) {
		System.out.println("$$$$$$$ createPmJob called $$$$$$$$");
		System.out.println("jsonMap=" + jsonMap);

		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		String self = "/v1/pm/jobs/" + UUID.randomUUID().toString();
		map.put(InterfaceConsts.PM_JOB_RESULT_SELF, self);
		map.put("rel", "self");
		mapList.add(map);
		map = new HashMap<String, Object>();
		map.put("uri", self + "/historyPmData");
		map.put("rel", "relations/historyPmData");
		mapList.add(map);
		resultMap.put(InterfaceConsts.PM_JOB_RESULT_LINKS, mapList);

		return Response.created(null).entity(resultMap).build();
	}

	@DELETE
	@Path("/pm/jobs/{jobId}")
	public Response deletePmJob(@PathParam("jobId") String jobId) {
		System.out.println("$$$$$$$ deletePmJob called $$$$$$$$");
		System.out.println("jobId=" + jobId);

		return Response.noContent().build();
	}

	@GET
	@Path("/VnfAlarm/{vnfInstanceID}")
	public String QueryVNFAlarm(@PathParam("vnfInstanceID") String vnfInstanceID) {
		System.out.println("$$$$$$$ QueryVNFAlarm called $$$$$$$$");
		System.out.println("vnfInstanceID=" + vnfInstanceID);

		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();

		try {
			reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(new File("c:/QueryVNFAlarm.txt")), "UTF-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append(System.getProperty("line.separator"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sb.toString();
	}
}
