package com.ericsson.skt.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Vn implements Serializable {
	private static final long serialVersionUID = -5460409168158574018L;

	@Id
	private String vnId;
	@Column(nullable = false, length = 100)
	private String vnName;
	@Column(nullable = true, length = 100)
	private String description;
	@Column(nullable = true, length = 100)
	private String ipAddressRange;
	@Column(nullable = false)
	private boolean dhcpEnabled;
	@Column(nullable = true, length = 100)
	private String ipVersion;
	@Column(nullable = true, length = 100)
	private String category;
	@Column(nullable = true, length = 100)
	private String networkType;
	@Column(nullable = true, length = 20)
	private String provisioningStatus;
	@Column(nullable = false)
	private boolean enabled;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIpAddressRange() {
		return ipAddressRange;
	}

	public void setIpAddressRange(String ipAddressRange) {
		this.ipAddressRange = ipAddressRange;
	}

	public boolean isDhcpEnabled() {
		return dhcpEnabled;
	}

	public void setDhcpEnabled(boolean dhcpEnabled) {
		this.dhcpEnabled = dhcpEnabled;
	}

	public String getIpVersion() {
		return ipVersion;
	}

	public void setIpVersion(String ipVersion) {
		this.ipVersion = ipVersion;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public String getProvisioningStatus() {
		return provisioningStatus;
	}

	public void setProvisioningStatus(String provisioningStatus) {
		this.provisioningStatus = provisioningStatus;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String toString() {
		return "Vn [vnId=" + vnId + ",vnName=" + vnName + ",description=" + description + ",ipAddressRange="
				+ this.ipAddressRange + ",dhcpEnabled=" + this.dhcpEnabled + ",ipVersion=" + ipVersion + ",category="
				+ category + ",networkType=" + networkType + ",provisioningStatus=" + provisioningStatus + ",enabled="
				+ enabled;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((vnId == null) ? 0 : vnId.hashCode());
		return result;
	}
}
