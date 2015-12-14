package com.ericsson.skt.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Vm implements Serializable {
	private static final long serialVersionUID = 1339875130239828485L;
	@Id
	private String vmId;
	@Column(nullable = false, length = 100)
	private String vmName;
	@Column(nullable = false, length = 100)
	private String vimObjectId;
	@Column(nullable = false, length = 100)
	private String hostId;
	@Column(nullable = false, length = 100)
	private String hostName;

	public String getVmId() {
		return vmId;
	}

	public void setVmId(String vmId) {
		this.vmId = vmId;
	}

	public String getVmName() {
		return vmName;
	}

	public void setVmName(String vmName) {
		this.vmName = vmName;
	}

	public String getVimObjectId() {
		return vimObjectId;
	}

	public void setVimObjectId(String vimObjectId) {
		this.vimObjectId = vimObjectId;
	}

	public String getHostId() {
		return hostId;
	}

	public void setHostId(String hostId) {
		this.hostId = hostId;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	@Override
	public String toString() {
		return "Vm [vmId=" + vmId + ",vmName=" + vmName + ",vimObjectId=" + vimObjectId + ",hostId=" + this.hostId
				+ ",hostName=" + hostName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((vmId == null) ? 0 : vmId.hashCode());
		return result;
	}
}
