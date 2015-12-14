package com.xmc.hospitalrec.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Vrf  implements Serializable{
	private static final long serialVersionUID = 8480165466206546214L;
	@ Id
	private String vrfId;
	@Column(nullable=false, length=100)
	private String vrfName;
	@Column(nullable=false)
	private boolean isExternallyConnected;
	@Column(nullable=false, length=20)
	private String provisioningStatus;
}
