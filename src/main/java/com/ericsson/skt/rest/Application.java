package com.ericsson.skt.rest;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import com.ericsson.skt.Skt;

public class Application extends ResourceConfig {

	public Application() {
		if(Skt.isDevelopmentProfile()) this.property( ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
		this.register( MultiPartFeature.class);
		this.packages( _RestPackage.class.getPackage().getName());
	}
}
