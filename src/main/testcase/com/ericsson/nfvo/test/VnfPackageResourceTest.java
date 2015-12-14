package com.ericsson.nfvo.test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.Test;

import junit.framework.TestCase;

public class VnfPackageResourceTest extends TestCase {
	@Test
	public void testUploadPackage() {
		Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

		WebTarget target = client.target(TestUtils.NFVO_URL).path("categories/vnfs/packages");

		FileDataBodyPart bodyPart = new FileDataBodyPart("file",
				new File("D:\\360°²È«ä¯ÀÀÆ÷ÏÂÔØ", "node-v0.12.5-x64.msi"));

		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
		formDataMultiPart.field("uploadOperator", "test");
		formDataMultiPart.field("domainId", "domain3");
		formDataMultiPart.field("packageName", "node-v0.12.5");
		formDataMultiPart.field("packageVersion", "v1.0.0");
		formDataMultiPart.field("packageDescription", "description package");
		formDataMultiPart.bodyPart(bodyPart);

		String result = target.request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(formDataMultiPart, formDataMultiPart.getMediaType()), String.class);

		System.out.println(result);
	}

	@Test
	public void testUpdateVnfPackage() {
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("id", 1);
		jsonMap.put("packageName", "pacakge1");
		jsonMap.put("packageDescription", "package description 1");

		WebTarget target = TestUtils.getWebTarget("categories/vnfs/packages/1");
		Response response = target.request().buildPut(Entity.entity(jsonMap, MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetVnfPackages() {
		WebTarget target = TestUtils.getWebTarget("categories/vnfs/packages");
		Response response = target.request().get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetVnfPackages_1() {
		WebTarget target = TestUtils.getWebTarget("categories/vnfs/packages/1");
		Response response = target.request().get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetVnfPackages_2() {
		WebTarget target = TestUtils.getWebTarget("categories/vnfs/packages");
		WebTarget targetWithQueryParam = target.queryParam("domainId", "domain1")
				.queryParam("status", 1)
				.queryParam("limit", "1")
				.queryParam("offset", "1");
		Invocation.Builder invocationBuilder = targetWithQueryParam.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testDuplicateProperties() {
		WebTarget target = TestUtils.getWebTarget("domains/duplicateproperties");
		WebTarget targetWithQueryParam = target.queryParam("name", "testVnfPackageName111").queryParam("identifier", "testVnfPackage");
		Invocation.Builder invocationBuilder = targetWithQueryParam.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testDeleteVnfPackage() {
		WebTarget target = TestUtils.getWebTarget("categories/vnfs/packages/5");
		Response response = target.request().delete();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testActiveVnfPackage() {
		WebTarget target = TestUtils.getWebTarget("categories/vnfs/packages/1/active");
		Response response = target.request().buildPut(Entity.entity("", MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testDeactiveVnfPackage() {
		WebTarget target = TestUtils.getWebTarget("categories/vnfs/packages/1/deactive");
		Response response = target.request().buildPut(Entity.entity("", MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetFiles() {
		WebTarget target = TestUtils.getWebTarget("categories/vnfs/packages/1/files");
		Response response = target.request().get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetImages() {
		WebTarget target = TestUtils.getWebTarget("categories/vnfs/packages/1/images");
		Response response = target.request().get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetImageStatus() {
		WebTarget target = TestUtils.getWebTarget("categories/vnfs/packages/1/images/1/status");
		Response response = target.request().get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testIssue() {
		Map<String, List<String>> paraMap = new HashMap<String, List<String>>();
		List<String> vimList = new ArrayList<String>();
		vimList.add("VIM1");
		vimList.add("VIM2");
		paraMap.put("vims", vimList);
		
		WebTarget target = TestUtils.getWebTarget("categories/vnfs/packages/5/issues");
		Response response = target.request().buildPut(Entity.entity(paraMap, MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testExport() {
		WebTarget target = TestUtils.getWebTarget("categories/vnfs/packages/5/export");
		Response response = target.request().get();
		System.out.println("status=" + response.getStatus());
		//System.out.println(response.readEntity(String.class));
		response.close();
	}
}
