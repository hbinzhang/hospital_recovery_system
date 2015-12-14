package com.ericsson.skt.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ericsson.skt.model.PmData;

@Repository("pmDao")
@Transactional(propagation = Propagation.SUPPORTS)
public class PmDao extends AbstractDao {

	@SuppressWarnings("rawtypes")
	public List queryLastPmData(String objectType, String objectId) {
		Query query = this.getEntityManager().createQuery("select d from PmData d where d.objectType='" + objectType
				+ "' and d.objectId like '%" + objectId + "%' and d.createAt=(select max(d1.createAt) from PmData d1)");
		List dataList = query.getResultList();
		return dataList;
	}

	@SuppressWarnings("unchecked")
	public List<PmData> queryHisPmData(String objectType, String objectId, String meterItem, String beginTime) {
		Query query = this.getEntityManager()
				.createQuery("select d from PmData d where d.objectType='" + objectType + "' and d.objectId like '%"
						+ objectId + "%' and d.meterItem='" + meterItem + "' and d.createAt>='" + beginTime
						+ "' order by createAt");
		List<PmData> dataList = (List<PmData>) query.getResultList();
		return dataList;
	}

	@SuppressWarnings("unchecked")
	public List<String> queryPmJobList(boolean vnfFlag) {
		String jpql = "select j.jobId from PmJob j where j.type";
		if (vnfFlag)
			jpql += "='VNF'";
		else
			jpql += "!='VNF'";

		Query query = this.getEntityManager().createQuery(jpql);
		List<String> dataList = (List<String>) query.getResultList();
		return dataList;
	}

	@SuppressWarnings("unchecked")
	public List<String> getVmIdList() {
		String jpql = "select distinct t.vmId from Vm t";
		Query query = this.getEntityManager().createQuery(jpql);
		List<String> dataList = (List<String>) query.getResultList();
		return dataList;
	}

	@SuppressWarnings("unchecked")
	public List<String> getHostNameList() {
		String jpql = "select distinct t.hostName from Vm t";
		Query query = this.getEntityManager().createQuery(jpql);
		List<String> dataList = (List<String>) query.getResultList();
		return dataList;
	}
}
