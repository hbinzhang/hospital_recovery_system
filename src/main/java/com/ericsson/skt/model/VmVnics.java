package com.ericsson.skt.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class VmVnics implements Serializable {
	private static final long serialVersionUID = 7898265162830672597L;
	@Id
	private String vnicId;
	@Column(nullable = false, length = 100)
	private String vnicName;
	@Column(nullable = false, length = 100)
	private String vmId;
	@Column(nullable = true, length = 100)
	private String vnId;
	@Column(nullable = true, length = 100)
	private String vnName;
	@Column(nullable = true, length = 100)
	private String macAddress;
	@Column(nullable = true, length = 255)
	private String internalIpAddress;
	@Column(nullable = true, length = 255)
	private String externalIpAddress;

	public String getVnicId() {
		return vnicId;
	}

	public void setVnicId(String vnicId) {
		this.vnicId = vnicId;
	}

	public String getVnicName() {
		return vnicName;
	}

	public void setVnicName(String vnicName) {
		this.vnicName = vnicName;
	}

	public String getVmId() {
		return vmId;
	}

	public void setVmId(String vmId) {
		this.vmId = vmId;
	}

	public String getVnId() {
		return vnId;
	}

	public void setVnId(String vnId) {
		this.vnId = vnId;
	}

	public String getVnName() {
		return vnName;
	}

	public void setVnName(String vnName) {
		this.vnName = vnName;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getInternalIpAddress() {
		return internalIpAddress;
	}

	public void setInternalIpAddress(String internalIpAddress) {
		this.internalIpAddress = internalIpAddress;
	}

	public String getExternalIpAddress() {
		return externalIpAddress;
	}

	public void setExternalIpAddress(String externalIpAddress) {
		this.externalIpAddress = externalIpAddress;
	}

	@Override
	public String toString() {
		return "VmVnics [vnicId=" + vnicId + ",vnicName=" + vnicName + ",vmId=" + vmId + ",vnId=" + this.vnId
				+ ",vnName=" + this.vnName + ",macAddress=" + macAddress + ",internalIpAddress=" + internalIpAddress
				+ ",externalIpAddress=" + externalIpAddress;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((vnicId == null) ? 0 : vnicId.hashCode());
		return result;
	}
}
