package com.ericsson.nfvo.test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

public class TestUtils {
	public final static String NFVO_URL = "http://localhost:8080/nfvo/rest/v1";

	public static WebTarget getWebTarget(String path) {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(NFVO_URL).path(path);

		return target;
	}

	public static WebTarget getMultiPartFeatureWebTarget(String path) {
		Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

		WebTarget target = client.target(NFVO_URL).path(path);
		return target;
	}
}
