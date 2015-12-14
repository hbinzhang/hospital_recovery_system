package com.ericsson.skt.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PmData implements Serializable {
	private static final long serialVersionUID = -5393907402812634924L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id = 0;
	@Column(nullable = false, length = 10)
	private String objectType;
	@Column(nullable = false, length = 100)
	private String objectId;
	@Column(nullable = false, length = 20)
	private String meterItem;
	@Column(nullable = false, length = 20)
	private String meterValue;
	@Column(nullable = false, length = 10)
	private String meterUnit;
	@Column(nullable = false, length = 20)
	private String createAt;

	public PmData() {
		
	}

	public PmData(String objectType, String objectId, String meterItem, String meterValue, String meterUnit, String createAt) {
		this.setObjectType(objectType);
		this.setObjectId(objectId);
		this.setMeterItem(meterItem);
		this.setMeterValue(meterValue);
		this.setMeterUnit(meterUnit);
		this.setCreateAt(createAt);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public String getMeterItem() {
		return meterItem;
	}

	public void setMeterItem(String meterItem) {
		this.meterItem = meterItem;
	}

	public String getMeterValue() {
		return meterValue;
	}

	public void setMeterValue(String meterValue) {
		this.meterValue = meterValue;
	}

	public String getMeterUnit() {
		return meterUnit;
	}

	public void setMeterUnit(String meterUnit) {
		this.meterUnit = meterUnit;
	}

	public String getCreateAt() {
		return createAt;
	}

	public void setCreateAt(String createAt) {
		this.createAt = createAt;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	@Override
	public String toString() {
		return "PmData [id=" + id + ",objectType=" + objectType + ",objectId=" + objectId + ",meterItem="
				+ this.meterItem + ",meterValue=" + this.meterValue + ",meterUnit=" + meterUnit + ",createAt="
				+ createAt;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((meterItem == null) ? 0 : meterItem.hashCode());
		result = prime * result + ((objectId == null) ? 0 : objectId.hashCode());
		return result;
	}

}
