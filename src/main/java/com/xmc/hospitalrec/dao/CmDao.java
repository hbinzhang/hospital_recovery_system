package com.xmc.hospitalrec.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xmc.hospitalrec.model.Vm;
import com.xmc.hospitalrec.model.VmVnics;

@Repository("cmDao")
@Transactional(propagation = Propagation.SUPPORTS)
public class CmDao extends AbstractDao {

	@SuppressWarnings("unchecked")
	public List<String> getHostNameList() {
		Query query = this.getEntityManager().createQuery("select distinct m.hostName from Vm m");
		return (List<String>) query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Vm> getVmList(String hostName) {
		Query query = this.getEntityManager().createQuery("select m from Vm m where m.hostName='" + hostName + "'");
		return (List<Vm>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<String> getVnNameList() {
		Query query = this.getEntityManager().createQuery("select n.vnName from Vn n");
		return (List<String>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<VmVnics> getVmVnicsList(String vmId) {
		Query query = this.getEntityManager().createQuery(
				"select n from Vm m,VmVnics n where m.vmId=n.vmId and m.vmId='" + vmId + "' and not n.vnId is null");
		return (List<VmVnics>) query.getResultList();
	}
}
