package com.xmc.hospitalrec.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class PmJob implements Serializable {
	private static final long serialVersionUID = 7168752747647575043L;
	@Id
	private String jobId;
	@Column(nullable = false, length = 100)
	private String nfvoId;
	@Column(nullable = false, length = 100)
	private String vimId;
	@Column(nullable = false, length = 10)
	private String period;
	@Column(nullable = false, length = 10)
	private String type;
	@Column(nullable = false, length = 255)
	private String kpi;
	@Lob
	@Basic(fetch = FetchType.LAZY)
	private String objecteId;

	@Override
	public String toString() {
		return "PmJob [jobId=" + jobId + ",nfvoId=" + nfvoId + ",vimId=" + vimId + ",period=" + this.period + ",type="
				+ this.type + ",kpi=" + kpi + ",objecteId=" + objecteId;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getNfvoId() {
		return nfvoId;
	}

	public void setNfvoId(String nfvoId) {
		this.nfvoId = nfvoId;
	}

	public String getVimId() {
		return vimId;
	}

	public void setVimId(String vimId) {
		this.vimId = vimId;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKpi() {
		return kpi;
	}

	public void setKpi(String kpi) {
		this.kpi = kpi;
	}

	public String getObjecteId() {
		return objecteId;
	}

	public void setObjecteId(String objecteId) {
		this.objecteId = objecteId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((jobId == null) ? 0 : jobId.hashCode());
		return result;
	}
}
