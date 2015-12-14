package com.xmc.hospitalrec.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CurAlarmVm implements Serializable {
	private static final long serialVersionUID = 8759409318406291808L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long sequenceNo;
	@Column(nullable = false, length = 255)
	private String component;
	@Column(nullable = false, length = 100)
	private String vmId;
	@Column(nullable = false, length = 100)
	private String timestamp;
	@Column(nullable = false, length = 20)
	private String type;
	@Column(nullable = true, length = 10)
	private String major;
	@Column(nullable = true, length = 20)
	private String minor;
	@Column(nullable = false, length = 10)
	private String severity;
	@Column(nullable = false, length = 100)
	private String probCause;
	@Column(nullable = true, length = 100)
	private String specificProblem;
	@Column(nullable = true, length = 100)
	private String additionalText;
	@Column(nullable = true, length = 100)
	private String additionalInfo;
	@Column(nullable = false)
	private boolean ackStatus = false;
	@Column(nullable = false)
	private boolean clearStatus = false;
	@Column(nullable = true, length = 100)
	private String clearTime;

	public long getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(long sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getVmId() {
		return vmId;
	}

	public void setVmId(String vmId) {
		this.vmId = vmId;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getMinor() {
		return minor;
	}

	public void setMinor(String minor) {
		this.minor = minor;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getProbCause() {
		return probCause;
	}

	public void setProbCause(String probCause) {
		this.probCause = probCause;
	}

	public String getSpecificProblem() {
		return specificProblem;
	}

	public void setSpecificProblem(String specificProblem) {
		this.specificProblem = specificProblem;
	}

	public String getAdditionalText() {
		return additionalText;
	}

	public void setAdditionalText(String additionalText) {
		this.additionalText = additionalText;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public boolean isAckStatus() {
		return ackStatus;
	}

	public void setAckStatus(boolean ackStatus) {
		this.ackStatus = ackStatus;
	}

	public boolean isClearStatus() {
		return clearStatus;
	}

	public void setClearStatus(boolean clearStatus) {
		this.clearStatus = clearStatus;
	}

	public String getClearTime() {
		return clearTime;
	}

	public void setClearTime(String clearTime) {
		this.clearTime = clearTime;
	}

	@Override
	public String toString() {
		return "CurAlarmVm [sequenceNo=" + sequenceNo + ",component=" + component + ",vmId=" + vmId + ",timestamp="
				+ this.timestamp + ",type=" + this.type + ",major=" + major + ",minor=" + minor + ",severity="
				+ this.severity + ",probCause=" + probCause + ",specificProblem=" + specificProblem + ",additionalText="
				+ additionalText + ",additionalInfo=" + additionalInfo + ",ackStatus=" + ackStatus + ",clearStatus="
				+ clearStatus + ",clearTime=" + clearTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((component == null) ? 0 : component.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		return result;
	}
}
