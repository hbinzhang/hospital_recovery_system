package com.ericsson.skt.rest.fm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
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

import com.ericsson.skt.dao.FmDao;
import com.ericsson.skt.model.CurAlarmVm;
import com.ericsson.skt.model.CurAlarmVnf;
import com.ericsson.skt.rest.util.Util;

/*
 * By default, Jersey instantiates resource classes in a per-request basis (the default and the only way standardized in JAX-RS).
 * However, when @Component is used, that is, when these resource classes are managed by Spring context, it becomes singleton. You cannot
 * use @Scope("request") to make it request-scoped, because the request life-cycle is not managed by Spring (i.e., the front-dispatcher
 * is not Spring). @Singleton annotation (which signals Jersey to use a singleton instance) is added here merely to remind this fact.
 */
@Singleton
@Component
@Path("fm")
@Transactional(propagation = Propagation.REQUIRED)
@Produces(MediaType.APPLICATION_JSON)
public class AlarmResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlarmResource.class);

	@Inject
	private FmDao dao;

	@SuppressWarnings("rawtypes")
	@GET
	@Path("/vm/{vmId}")
	public Response getCurAlarmVm(@PathParam("vmId") String vmId) {

		LOGGER.info("[getCurAlarmVm] vmId=" + vmId);

		List alarmList = this.dao.getCurAlarmVm(vmId);

		return Response.ok().entity(Util.genPageQueryResult(alarmList.size(), alarmList)).build();
	}

	@SuppressWarnings("rawtypes")
	@GET
	@Path("/vnf/{vnfId}")
	public Response getCurAlarmVnf(@PathParam("vnfId") String vnfId) {

		LOGGER.info("[getCurAlarmVnf] vnfId=" + vnfId);

		List alarmList = this.dao.getCurAlarmVnf(vnfId);

		return Response.ok().entity(Util.genPageQueryResult(alarmList.size(), alarmList)).build();
	}

	@GET
	@Path("/vnf/{vnfId}/count")
	public Response getCurAlarmVnfCount(@PathParam("vnfId") String vnfId) {

		LOGGER.info("[getCurAlarmVnfCount] vnfId=" + vnfId);

		long count = this.dao.getCurAlarmVnfCount(vnfId);
		Map<String, Long> resultMap = new HashMap<String, Long>();
		resultMap.put("data", count);

		return Response.ok().entity(resultMap).build();
	}

	@POST
	@Path("/vm/{sequenceNo}/ack")
	public Response ackAlarmVm(@PathParam("sequenceNo") long sequenceNo) {
		LOGGER.info("[ackAlarmVm] sequenceNo=" + sequenceNo);

		CurAlarmVm alarm = (CurAlarmVm) this.dao.find(CurAlarmVm.class, sequenceNo);
		if (alarm == null)
			return Response.status(Status.NOT_FOUND).build();

		alarm.setAckStatus(true);
		this.dao.update(alarm);

		return Response.created(null).entity(alarm).build();
	}

	@POST
	@Path("/vnf/{sequenceNo}/ack")
	public Response ackAlarmVnf(@PathParam("sequenceNo") long sequenceNo) {
		LOGGER.info("[ackAlarmVnf] sequenceNo=" + sequenceNo);

		CurAlarmVnf alarm = (CurAlarmVnf) this.dao.find(CurAlarmVnf.class, sequenceNo);
		if (alarm == null)
			return Response.status(Status.NOT_FOUND).build();

		alarm.setAckStatus(true);
		this.dao.update(alarm);

		return Response.created(null).entity(alarm).build();
	}

	@POST
	@Path("/vm/{sequenceNo}/clear")
	public Response clearAlarmVm(@PathParam("sequenceNo") long sequenceNo) {
		LOGGER.info("[clearAlarmVm] sequenceNo=" + sequenceNo);

		CurAlarmVm alarm = (CurAlarmVm) this.dao.find(CurAlarmVm.class, sequenceNo);
		if (alarm == null)
			return Response.status(Status.NOT_FOUND).build();

		alarm.setClearStatus(true);
		alarm.setClearTime(Util.getNowString());
		this.dao.update(alarm);

		return Response.created(null).entity(alarm).build();
	}

	@POST
	@Path("/vnf/{sequenceNo}/clear")
	public Response clearAlarmVnf(@PathParam("sequenceNo") long sequenceNo) {
		LOGGER.info("[clearAlarmVnf] sequenceNo=" + sequenceNo);

		CurAlarmVnf alarm = (CurAlarmVnf) this.dao.find(CurAlarmVnf.class, sequenceNo);
		if (alarm == null)
			return Response.status(Status.NOT_FOUND).build();

		alarm.setClearStatus(true);
		alarm.setClearTime(Util.getNowString());
		this.dao.update(alarm);

		return Response.created(null).entity(alarm).build();
	}

	@POST
	@Path("/vm/ack")
	public Response ackAlarmVms(@QueryParam("sequenceNo") String sequenceNo) {
		LOGGER.info("[ackAlarmVms] sequenceNo=" + sequenceNo);

		List<CurAlarmVm> alarmList = this.dao.ackAlarmVms(sequenceNo);

		return Response.created(null).entity(Util.genPageQueryResult(alarmList.size(), alarmList)).build();
	}

	@POST
	@Path("/vm/clear")
	public Response clearAlarmVms(@QueryParam("sequenceNo") String sequenceNo) {
		LOGGER.info("[clearAlarmVms] sequenceNo=" + sequenceNo);

		List<CurAlarmVm> alarmList = this.dao.clearAlarmVms(sequenceNo, Util.getNowString());

		return Response.created(null).entity(Util.genPageQueryResult(alarmList.size(), alarmList)).build();
	}

	@POST
	@Path("/vnf/ack")
	public Response ackAlarmVnfs(@QueryParam("sequenceNo") String sequenceNo) {
		LOGGER.info("[ackAlarmVnfs] sequenceNo=" + sequenceNo);

		List<CurAlarmVnf> alarmList = this.dao.ackAlarmVnfs(sequenceNo);

		return Response.created(null).entity(Util.genPageQueryResult(alarmList.size(), alarmList)).build();
	}

	@POST
	@Path("/vnf/clear")
	public Response clearAlarmVnfs(@QueryParam("sequenceNo") String sequenceNo) {
		LOGGER.info("[clearAlarmVnfs] sequenceNo=" + sequenceNo);

		List<CurAlarmVnf> alarmList = this.dao.clearAlarmVnfs(sequenceNo, Util.getNowString());

		return Response.created(null).entity(Util.genPageQueryResult(alarmList.size(), alarmList)).build();
	}
}
