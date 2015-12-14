package com.xmc.hospitalrec.rest.pm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xmc.hospitalrec.dao.PmDao;
import com.xmc.hospitalrec.model.PmData;
import com.xmc.hospitalrec.model.PmJob;
import com.xmc.hospitalrec.rest.util.InterfaceConsts;
import com.xmc.hospitalrec.rest.util.Message;
import com.xmc.hospitalrec.rest.util.Util;

/*
 * By default, Jersey instantiates resource classes in a per-request basis (the default and the only way standardized in JAX-RS).
 * However, when @Component is used, that is, when these resource classes are managed by Spring context, it becomes singleton. You cannot
 * use @Scope("request") to make it request-scoped, because the request life-cycle is not managed by Spring (i.e., the front-dispatcher
 * is not Spring). @Singleton annotation (which signals Jersey to use a singleton instance) is added here merely to remind this fact.
 */
@Singleton
@Component
@Path("pm")
@Transactional(propagation = Propagation.REQUIRED)
@Produces(MediaType.APPLICATION_JSON)
public class PmDataResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(PmDataResource.class);

	@Inject
	private PmDao dao;

	@SuppressWarnings("unchecked")
	@GET
	@Path("/data/{objectId}")
	public Response getPmData(@PathParam("objectId") String objectId, @QueryParam("objectType") String objectType,
			@QueryParam("meterItem") String meterItem, @QueryParam("beginTime") String beginTime,
			@QueryParam("history") boolean isHistory) {

		LOGGER.info("[getPmData] objectType=" + objectType + ",objectId=" + objectId + ",meterItem=" + meterItem
				+ ",isHistory=" + isHistory + ",beginTime=" + beginTime);

		List<PmData> pmdataList = null;
		if (isHistory) {
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			GregorianCalendar gc = new GregorianCalendar();

			pmdataList = this.dao.queryHisPmData(objectType, objectId, meterItem, beginTime);
			List<Map<Long, String>> resultList = new ArrayList<Map<Long, String>>();
			for (PmData data : pmdataList) {
				Map<Long, String> map = new HashMap<Long, String>();
				try {
					gc.setTime(sf.parse(data.getCreateAt()));
					map.put(gc.getTimeInMillis(), data.getMeterValue());
					resultList.add(map);
				} catch (ParseException e) {
					LOGGER.error("[getPmData] time format error.", e);
				}
			}
			return Response.ok().entity(Util.genPageQueryResult(resultList.size(), resultList)).build();
		} else {
			pmdataList = this.dao.queryLastPmData(objectType, objectId);

			return Response.ok().entity(Util.genPageQueryResult(pmdataList.size(), pmdataList)).build();
		}
	}

//	 KPIReport=[{
//		objectId=ManagedElement=76vCSCF,boardid=0, 
//		KPI=[{meterItem=averageCpuUsage, meterValue=15, meterUnit=}, 
//		     {meterItem=memoryUsed, meterValue=1218234343, meterUnit=}, 
//		     {meterItem=memory, meterValue=1212233333333, meterUnit=}]}, 
//	    {objectId=ManagedElement=76vCSCF, 
//	    KPI=[{meterItem=, meterValue=12121212, meterUnit=null}, 
//	         {meterItem=, meterValue=12177777, meterUnit=null}]}]}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GET
	@Path("/getboards/{vnfname}")
	public Response getPmBoardObjects(@PathParam("vnfname") String objectId,
			@QueryParam("objectType") String objectType) {
		LOGGER.info("[getPmBoardObjects] objectType=" + objectType + ",objectId=" + objectId);
		List pmdataList = null;
		pmdataList = this.dao.queryLastPmData(objectType, objectId);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("[getPmBoardObjects] get pmdata return: " + pmdataList);
		}
		List<Map> boardIds = new ArrayList<Map>();
		if (pmdataList != null && pmdataList.size() > 0) {
			for (PmData oneData : (List<PmData>) pmdataList) {
				String objid = oneData.getObjectId();
				if (objid.contains(InterfaceConsts.PM_NOTIFICATION_OBJ_SECTOR_BOARDID + "=")) {
					// it's board data
					Map boardid = new HashMap();
					boardid.put("boardId", objid);
					boardIds.add(boardid);
				}
			}
		}
		LOGGER.info("[getPmBoardObjects] return: " + boardIds);
		return Response.ok().entity(Util.genPageQueryResult(boardIds.size(), boardIds)).build();
	}

	@SuppressWarnings("unchecked")
	@POST
	@Path("/{jobId}/notification")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response performanceMetricsNotify(@PathParam("jobId") String jobId, Map<String, Object> jsonMap) {
		LOGGER.info("[performanceMetricsNotify] jobId=" + jobId + ",jsonMap=" + jsonMap);
		Object job = dao.getUnique(PmJob.class, "jobId", jobId);
		if (job == null) {
			LOGGER.warn("[performanceMetricsNotify] job not exist" + jobId);
			return Response.status(Status.BAD_REQUEST.getStatusCode())
					.entity(new Message("job dost not exist")).build();
		}
		if (jsonMap == null) {
			return Response.status(Status.BAD_REQUEST.getStatusCode())
					.entity(new Message("no pm data found, json map is required")).build();
		}

		PmData pmData = null;
		List<PmData> pmDataList = new ArrayList<PmData>();
		String createAt = (String) jsonMap.get(InterfaceConsts.PM_NOTIFICATION_CREATEAT);
		String objectId = null, objectType = null, meterItem = null, meterValue = null, meterUnit = null;

		List<Map<String, Object>> kpiReportList = (List<Map<String, Object>>) jsonMap
				.get(InterfaceConsts.PM_NOTIFICATION_KPIREPORT);
		for (Map<String, Object> kpiReportMap : kpiReportList) {
			objectId = (String) kpiReportMap.get(InterfaceConsts.PM_NOTIFICATION_OBJECTID);
			List<Map<String, String>> kpiMapList = (List<Map<String, String>>) kpiReportMap
					.get(InterfaceConsts.PM_NOTIFICATION_KPI);
			for (Map<String, String> kpiMap : kpiMapList) {
				meterItem = kpiMap.get(InterfaceConsts.PM_NOTIFICATION_METERITEM);
				meterUnit = kpiMap.get(InterfaceConsts.PM_NOTIFICATION_METERUNIT);
				meterValue = kpiMap.get(InterfaceConsts.PM_NOTIFICATION_NETERVALUE);

				String type = this.getObjectTypeByMeterItem(meterItem, objectId);
				if (type != null)
					objectType = type;

				pmData = new PmData(objectType, objectId, meterItem, meterValue, meterUnit, createAt);
				pmDataList.add(pmData);
			}
		}
		for (PmData data : pmDataList) {
			if (data.getObjectType() == null)
				data.setObjectType(objectType);
		}

		this.dao.insert(pmDataList);

		return Response.created(null).entity(null).build();
	}

	private String getObjectTypeByMeterItem(String meterItem, String objectId) {
		switch (meterItem) {
		case "MEM_UTIL" :
			return "HOST";
		case "CPU_UTIL" :
			return "HOST";
		case "network.outgoing.bytes" :
			return "VM";
		case "network.incoming.bytes" :
			return "VM";
		case "averageCpuUsage" :
			if (objectId.contains("vm=")) {
				return "VM";
			} else {
				return "VNF";
			}
		case "memoryUsed" :
			return "VNF";
		case "ggsnDownlinkBytes" :
			return "VNF";
		case "ggsnUplinkBytes" :
			return "VNF";
		default :
			return null;
		}
	}
}
