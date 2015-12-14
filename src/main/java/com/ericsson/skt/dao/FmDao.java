package com.ericsson.skt.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ericsson.skt.model.CurAlarmVm;
import com.ericsson.skt.model.CurAlarmVnf;
import com.ericsson.skt.rest.util.Util;

@Repository("fmDao")
@Transactional(propagation = Propagation.SUPPORTS)
public class FmDao extends AbstractDao {

	@SuppressWarnings("rawtypes")
	public List getCurAlarmVm(String vmName) {
		Query query = this.getEntityManager()
				.createQuery("select a from CurAlarmVm a, Vm b where a.vmId=b.vmId and b.vmName = '" + vmName
						+ "' order by a.sequenceNo desc");
		return query.getResultList();
	}

	@SuppressWarnings("rawtypes")
	public List getCurAlarmVnf(String vnfId) {
		Query query = this.getEntityManager()
				.createQuery("select a from CurAlarmVnf a where a.vnfId='" + vnfId + "' order by a.sequenceNo desc");
		return query.getResultList();
	}

	public long getCurAlarmVnfCount(String vnfId) {
		Query query = this.getEntityManager()
				.createQuery("select count(a) from CurAlarmVnf a where a.vnfId='" + vnfId + "' and a.clearStatus=0");
		return (long) query.getSingleResult();
	}

	public void saveCurAlarmVnf(List<CurAlarmVnf> alarmList) {
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		Map<String, Object> valueMap = new HashMap<String, Object>();

		valueMap.put("clearStatus", true);
		valueMap.put("clearTime", Util.getNowString());
		conditionMap.put("clearStatus", false);
		super.update(CurAlarmVnf.class, conditionMap, valueMap);

		if (alarmList == null)
			return;

		valueMap.put("clearStatus", false);
		valueMap.put("clearTime", null);
		conditionMap.clear();
		for (CurAlarmVnf alarm : alarmList) {
			conditionMap.put("alarmName", alarm.getAlarmName());
			conditionMap.put("timestamp", alarm.getTimestamp());
			conditionMap.put("component", alarm.getComponent());

			int count = super.update(CurAlarmVnf.class, conditionMap, valueMap);
			if (count == 0)
				super.insert(alarm);
		}
	}

	public int deleteOvertimeAlarmVnf(String alarmTime) {
		Query query = this.getEntityManager()
				.createQuery("delete from CurAlarmVnf a where a.timestamp<='" + alarmTime + "'");
		return query.executeUpdate();
	}

	public int deleteOvertimeAlarmVm(String alarmTime) {
		Query query = this.getEntityManager()
				.createQuery("delete from CurAlarmVm a where a.timestamp<='" + alarmTime + "'");
		return query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	public List<CurAlarmVm> ackAlarmVms(String sequenceNo) {
		Query query = this.getEntityManager()
				.createQuery("update CurAlarmVm a set a.ackStatus=1 where a.sequenceNo in (" + sequenceNo + ")");
		query.executeUpdate();
		query = this.getEntityManager()
				.createQuery("select a from CurAlarmVm a where a.sequenceNo in (" + sequenceNo + ")");
		return (List<CurAlarmVm>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<CurAlarmVm> clearAlarmVms(String sequenceNo, String clearTime) {
		Query query = this.getEntityManager().createQuery("update CurAlarmVm a set a.clearStatus=1,clearTime='"
				+ clearTime + "' where a.sequenceNo in (" + sequenceNo + ")");
		query.executeUpdate();
		query = this.getEntityManager()
				.createQuery("select a from CurAlarmVm a where a.sequenceNo in (" + sequenceNo + ")");
		return (List<CurAlarmVm>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<CurAlarmVnf> ackAlarmVnfs(String sequenceNo) {
		Query query = this.getEntityManager()
				.createQuery("update CurAlarmVnf a set a.ackStatus=1 where a.sequenceNo in (" + sequenceNo + ")");
		query.executeUpdate();
		query = this.getEntityManager()
				.createQuery("select a from CurAlarmVnf a where a.sequenceNo in (" + sequenceNo + ")");
		return (List<CurAlarmVnf>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<CurAlarmVnf> clearAlarmVnfs(String sequenceNo, String clearTime) {
		Query query = this.getEntityManager().createQuery("update CurAlarmVnf a set a.clearStatus=1,clearTime='"
				+ clearTime + "' where a.sequenceNo in (" + sequenceNo + ")");
		query.executeUpdate();
		query = this.getEntityManager()
				.createQuery("select a from CurAlarmVnf a where a.sequenceNo in (" + sequenceNo + ")");
		return (List<CurAlarmVnf>) query.getResultList();
	}

	public void clearVmAlarm(CurAlarmVm alarm) {
		Query query = this.getEntityManager()
				.createQuery("update CurAlarmVm a set a.clearStatus=1,clearTime='" + alarm.getTimestamp()
						+ "' where a.major='" + alarm.getMajor() + "' and a.minor='" + alarm.getMinor()
						+ "' and a.component='" + alarm.getComponent() + "' and a.clearStatus=0");
		query.executeUpdate();
	}
}
