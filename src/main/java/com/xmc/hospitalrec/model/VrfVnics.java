package com.xmc.hospitalrec.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class VrfVnics  implements Serializable{
	private static final long serialVersionUID = 7898265162830672597L;
	@ Id
	private String vnicId;
	@Column(nullable=false, length=100)
	private String vnicName;
	@Column(nullable=false, length=100)
	private String vrfId;
	@Column(nullable=false)
	private boolean internalFlag;
}
