package com.xmc.hospitalrec.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class BackupEvent implements Serializable {
	private static final long serialVersionUID = -1772658362825936161L;
	@Id
	private long id;
	@Column(nullable = false, length = 100)
	private String vnfName;
	@Column(nullable = false, length = 20)
	private String time;
	@Column(nullable = false, length = 20)
	private String event;
	@Column(nullable = false, length = 20)
	private String status;
	@Column(nullable = true, length = 20)
	private String dump;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getVnfName() {
		return vnfName;
	}

	public void setVnfName(String vnfName) {
		this.vnfName = vnfName;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDump() {
		return dump;
	}

	public void setDump(String dump) {
		this.dump = dump;
	}

	@Override
	public String toString() {
		return "BackupEvent [id=" + id + ",vnfName=" + vnfName + ",time=" + time;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((vnfName == null) ? 0 : vnfName.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		return result;
	}
}
