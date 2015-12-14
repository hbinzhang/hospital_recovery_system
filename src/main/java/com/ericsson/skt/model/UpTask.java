package com.ericsson.skt.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UpTask implements Serializable {

	private static final long serialVersionUID = -1435463434l;
	
	@Id
	@Column(nullable = false, length = 100)
	private String vnfName;
	@Column(nullable = false, length = 100)
	private String swName;
	@Column(nullable = false, length = 30)
	private String scheduleTime;
	@Column(nullable = false, length = 2)
	private String thresholdType;
	@Column(nullable = false, length = 10)
	private String timeConsumed;
	@Column(nullable = false, length = 100)
	private String userRemain;
	public UpTask() {
		super();
	}
	public String getVnfName() {
		return vnfName;
	}
	public void setVnfName(String vnfName) {
		this.vnfName = vnfName;
	}
	public String getSwName() {
		return swName;
	}
	public void setSwName(String swName) {
		this.swName = swName;
	}
	public String getScheduleTime() {
		return scheduleTime;
	}
	public void setScheduleTime(String scheduleTime) {
		this.scheduleTime = scheduleTime;
	}
	public String getThresholdType() {
		return thresholdType;
	}
	public void setThresholdType(String thresholdType) {
		this.thresholdType = thresholdType;
	}
	public String getTimeConsumed() {
		return timeConsumed;
	}
	public void setTimeConsumed(String timeConsumed) {
		this.timeConsumed = timeConsumed;
	}
	public String getUserRemain() {
		return userRemain;
	}
	public void setUserRemain(String userRemain) {
		this.userRemain = userRemain;
	}
	@Override
	public String toString() {
		return "UpTask [vnfName=" + vnfName + ", swName=" + swName + ", scheduleTime=" + scheduleTime
				+ ", thresholdType=" + thresholdType + ", timeConsumed=" + timeConsumed + ", userRemain=" + userRemain
				+ "]";
	}
	
	
}
