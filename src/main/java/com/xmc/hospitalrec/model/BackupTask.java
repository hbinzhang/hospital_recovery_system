package com.xmc.hospitalrec.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class BackupTask implements Serializable {
	private static final long serialVersionUID = -4671648913953970485L;

	public static final String PERIOD_DAY = "Every Day";
	public static final String PERIOD_WEEK = "Every Week";
	public static final String PERIOD_MONTH = "Every Month";

	@Id
	@Column(nullable = false, length = 100)
	private String vnfName;
	@Column(nullable = false, length = 20)
	private String period;
	@Column(nullable = false, length = 20)
	private String triggerTime;

	public String getVnfName() {
		return vnfName;
	}

	public void setVnfName(String vnfName) {
		this.vnfName = vnfName;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getTriggerTime() {
		return triggerTime;
	}

	public void setTriggerTime(String triggerTime) {
		this.triggerTime = triggerTime;
	}

	@Override
	public String toString() {
		return "BackupTask [vnfName=" + vnfName + ",period=" + period + ",triggerTime=" + triggerTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((vnfName == null) ? 0 : vnfName.hashCode());
		return result;
	}
}
