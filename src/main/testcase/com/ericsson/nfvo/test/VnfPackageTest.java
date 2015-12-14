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

public class VnfPackageTest extends TestCase {
	private final static String NFVO_URL = "http://localhost:8080/nfvo/rest/v1";

	@Test
	public static void testUploadPackage1() {
		Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

		WebTarget target = client.target(NFVO_URL).path("categories/vnfs/packages");

		FileDataBodyPart bodyPart = new FileDataBodyPart("file",
				new File("D:\\360安全浏览器下载", "jdk-8u45-linux-x64.tar.gz"));

		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
		formDataMultiPart.field("uploadOperator", "test");
		formDataMultiPart.field("domainId", "domain1");
		formDataMultiPart.field("packageName", "jdk-8u45-linux-x64");
		formDataMultiPart.field("packageVersion", "v1.0.0");
		formDataMultiPart.field("packageDescription", "description package");
		formDataMultiPart.bodyPart(bodyPart);

		String result = target.request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(formDataMultiPart, formDataMultiPart.getMediaType()), String.class);

		System.out.println(result);
	}

	@Test
	public static void testUploadPackage2() {
		Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

		WebTarget target = client.target(NFVO_URL).path("categories/vnfs/packages");

		FileDataBodyPart bodyPart = new FileDataBodyPart("file",
				new File("D:\\360安全浏览器下载", "node-v0.12.5-x64.msi"));

		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
		formDataMultiPart.field("uploadOperator", "test");
		formDataMultiPart.field("domainId", "domain2");
		formDataMultiPart.field("packageName", "node-v0.12.5-x64.msi");
		formDataMultiPart.field("packageVersion", "v1.0.0");
		formDataMultiPart.field("packageDescription", "description package");
		formDataMultiPart.bodyPart(bodyPart);

		String result = target.request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(formDataMultiPart, formDataMultiPart.getMediaType()), String.class);

		System.out.println(result);
	}
}
