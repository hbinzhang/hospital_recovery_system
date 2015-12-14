package com.ericsson.skt.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CurAlarmVnf implements Serializable {
	private static final long serialVersionUID = 8759409318406291808L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long sequenceNo;
	@Column(nullable = false, length = 100)
	private String component;
	@Column(nullable = false, length = 100)
	private String vnfId;
	@Column(nullable = false, length = 100)
	private String alarmName;
	@Column(nullable = true, length = 100)
	private String type;
	@Column(nullable = false, length = 10)
	private String severity;
	@Column(nullable = true, length = 100)
	private String probCause;
	@Column(nullable = false, length = 20)
	private String timestamp;
	@Column(nullable = false, length = 1000)
	private String alarmBody;
	@Column(nullable = false)
	private boolean ackStatus;
	@Column(nullable = false)
	private boolean clearStatus;
	@Column(nullable = true, length = 20)
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

	public String getVnfId() {
		return vnfId;
	}

	public void setVnfId(String vnfId) {
		this.vnfId = vnfId;
	}

	public String getAlarmName() {
		return alarmName;
	}

	public void setAlarmName(String alarmName) {
		this.alarmName = alarmName;
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

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getAlarmBody() {
		return alarmBody;
	}

	public void setAlarmBody(String alarmBody) {
		this.alarmBody = alarmBody;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getClearTime() {
		return clearTime;
	}

	public void setClearTime(String clearTime) {
		this.clearTime = clearTime;
	}

	@Override
	public String toString() {
		return "CurAlarmVnf [sequenceNo=" + sequenceNo + ",component=" + component + ",vnfId=" + vnfId + ",timestamp="
				+ this.timestamp + ",alarmName=" + this.alarmName + ",severity=" + this.severity + ",type=" + type
				+ ",probCause=" + probCause + ",alarmBody=" + alarmBody + ",ackStatus=" + ackStatus + ",clearStatus="
				+ clearStatus + ",clearTime=" + clearTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((component == null) ? 0 : component.hashCode());
		result = prime * result + ((alarmName == null) ? 0 : alarmName.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		return result;
	}
}
