package com.xmc.hospitalrec.rest.cm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xmc.hospitalrec.Configure;
import com.xmc.hospitalrec.dao.CmDao;
import com.xmc.hospitalrec.model.Vm;
import com.xmc.hospitalrec.model.VmVnics;
import com.xmc.hospitalrec.model.Vn;
import com.xmc.hospitalrec.rest.util.InterfaceConsts;
import com.xmc.hospitalrec.rest.util.Util;

@Component
@Transactional(propagation = Propagation.REQUIRED)
public class CmService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CmService.class);
	@Inject
	private CmDao dao;

	public void updateNetwork() {
		Map<String, Object> map = this.invokeCmgwInterface(Configure.getVnfInstanceId(), Configure.getCmgwUrl());
		if (map == null)
			return;

		List<Vn> vnList = new ArrayList<Vn>();
		List<Vm> vmList = new ArrayList<Vm>();
		List<VmVnics> vmVnicsList = new ArrayList<VmVnics>();

		this.fetchVnList(map, vnList);
		this.dao.delete(Vn.class, null);
		this.dao.insert(vnList);

		this.fetchVmList(map, vmList, vmVnicsList);
		this.dao.delete(Vm.class, null);
		this.dao.insert(vmList);
		this.dao.delete(VmVnics.class, null);
		this.dao.insert(vmVnicsList);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> invokeCmgwInterface(String vnfInstanceID, String cmgwUrl) {
		Map<String, Object> map = null;
		WebTarget target = Util.getWebTarget("/v1/VNFnetwork/" + vnfInstanceID, cmgwUrl);
		LOGGER.debug("[invokeCmgwInterface] target=" + target.toString());

		Response response = target.request().get();
		int status = response.getStatus();
		LOGGER.debug("[invokeCmgwInterface] return status=" + status + ", 200 is expected");

		if (status == Status.OK.getStatusCode()) {
			map = response.readEntity(Map.class);
			LOGGER.debug("[invokeCmgwInterface] return entity=" + map);
		}
		response.close();
		return map;
	}

	@SuppressWarnings("unchecked")
	private void fetchVnList(Map<String, Object> map, List<Vn> vnList) {
		List<Map<String, Object>> retVnMapList = (List<Map<String, Object>>) map.get(InterfaceConsts.NETWORK_VNLIST);
		for (Map<String, Object> vnMap : retVnMapList) {
			Vn vn = new Vn();

			vn.setVnId((String) vnMap.get(InterfaceConsts.NETWORK_ID));
			vn.setVnName((String) vnMap.get(InterfaceConsts.NETWORK_NAME));
			vn.setCategory((String) vnMap.get(InterfaceConsts.NETWORK_CATEGORY));
			vn.setDescription((String) vnMap.get(InterfaceConsts.NETWORK_DESCRIPTION));
			vn.setDhcpEnabled((boolean) vnMap.getOrDefault(InterfaceConsts.NETWORK_DHCPENABLED, false));
			vn.setEnabled((boolean) vnMap.getOrDefault(InterfaceConsts.NETWORK_ENABLED, false));
			vn.setIpAddressRange((String) vnMap.get(InterfaceConsts.NETWORK_IPADDRESSRANGE));
			vn.setIpVersion((String) vnMap.get(InterfaceConsts.NETWORK_IPVERSION));
			vn.setNetworkType((String) vnMap.get(InterfaceConsts.NETWORK_NETWORKTYPE));
			vn.setProvisioningStatus((String) vnMap.get(InterfaceConsts.NETWORK_PROVISIONINGSTATUS));

			vnList.add(vn);
		}
	}

	@SuppressWarnings("unchecked")
	private void fetchVmList(Map<String, Object> map, List<Vm> vmList, List<VmVnics> vmVnicsList) {
		List<Map<String, Object>> retVmMapList = (List<Map<String, Object>>) map.get(InterfaceConsts.NETWORK_VMLIST);
		for (Map<String, Object> vmMap : retVmMapList) {
			Vm vm = new Vm();

			vm.setVmId((String) vmMap.get(InterfaceConsts.NETWORK_VIMOBJECTID));
			vm.setVmName((String) vmMap.get(InterfaceConsts.NETWORK_NAME));
			vm.setVimObjectId((String) vmMap.get(InterfaceConsts.NETWORK_VIMOBJECTID));
			vm.setHostId((String) vmMap.get(InterfaceConsts.NETWORK_HOSTID));
			vm.setHostName((String) vmMap.get(InterfaceConsts.NETWORK_HOSTNAME));

			vmList.add(vm);

			List<Map<String, Object>> retVmVnicsMapList = (List<Map<String, Object>>) vmMap
					.get(InterfaceConsts.NETWORK_VMVNICS);
			if (retVmVnicsMapList == null)
				continue;

			for (Map<String, Object> vmVnicsMap : retVmVnicsMapList) {
				VmVnics vmVnics = new VmVnics();

				vmVnics.setVnicId((String) vmVnicsMap.get(InterfaceConsts.NETWORK_ID));
				vmVnics.setVnicName((String) vmVnicsMap.get(InterfaceConsts.NETWORK_NAME));
				vmVnics.setVmId(vm.getVmId());
				vmVnics.setExternalIpAddress((String) vmVnicsMap.get(InterfaceConsts.NETWORK_EXTERNALIPADDRESS));
				vmVnics.setMacAddress((String) vmVnicsMap.get(InterfaceConsts.NETWORK_MACADDRESS));

				Map<String, String> vnMap = (Map<String, String>) vmVnicsMap.get(InterfaceConsts.NETWORK_VN);
				if (vnMap != null) {
					vmVnics.setVnName(vnMap.get(InterfaceConsts.NETWORK_NAME));
					vmVnics.setVnId(vnMap.get(InterfaceConsts.NETWORK_ID));
				}

				List<String> iList = (List<String>) vmVnicsMap.get(InterfaceConsts.NETWORK_INTERNALIPADDRESS);
				if (iList != null)
					vmVnics.setInternalIpAddress(iList.toString());
				vmVnicsList.add(vmVnics);
			}
		}
	}
}
