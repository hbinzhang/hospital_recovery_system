package com.ericsson.nfvo.test;

import java.io.File;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.Test;

import junit.framework.TestCase;

public class PolicyTest extends TestCase {
	@Test
	public void testCreatePolicy() {
		Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

		WebTarget target = client.target("http://localhost:8080/nfvo/rest/v1").path("policies/vnfms/vnfm2/vnfs/vnf2");

		FileDataBodyPart bodyPart = new FileDataBodyPart("file", new File("D:\\", "policy.xml"));

		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
		formDataMultiPart.field("domainId", "domain1");
		formDataMultiPart.field("version", "v1.0.0");
		formDataMultiPart.field("name", "vnf2_policy");
		formDataMultiPart.field("operator", "user1");
		formDataMultiPart.field("description", "policy.xml");
		formDataMultiPart.bodyPart(bodyPart);

		String result = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(formDataMultiPart, formDataMultiPart.getMediaType()), String.class);

		System.out.println(result);

	}

}
