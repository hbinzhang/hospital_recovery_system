package com.ericsson.nfvo.test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.ericsson.nfvo.model.system.Vnfm;

import junit.framework.TestCase;

public class VnfmResourceTest extends TestCase {
	@Test
	public void testCreateVnfm() {
		Vnfm obj = new Vnfm();

		obj.setIdentifier("testVnfm");
		obj.setName("testVnfmName");
		obj.setDescription("test vim description");
		obj.setDomainId("domain1");
		obj.setServerURL("http://localhost:8080/vfvo/rest/v1");
		obj.setPassword("666666");
		obj.setUserName("user1");
		obj.setVendor("ericsson");
		obj.setVersion("v2.0.0");

		WebTarget target = TestUtils.getWebTarget("vnfms");
		Response response = target.request().buildPost(Entity.entity(obj, MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testUpdateVnfm() {
		Vnfm obj = new Vnfm();

		obj.setIdentifier("testVnfm");
		obj.setName("testVnfmName");
		obj.setDescription("test vim description");
		obj.setDomainId("domain1");
		obj.setServerURL("http://localhost:8080/vfvo/rest/v1");
		obj.setPassword("888888");
		obj.setUserName("user2");
		obj.setVendor("ericsson");
		obj.setVersion("v2.0.1");

		WebTarget target = TestUtils.getWebTarget("vnfms/testVnfm");
		Response response = target.request().buildPut(Entity.entity(obj, MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetVnfm() {
		WebTarget target = TestUtils.getWebTarget("vnfms/testVnfm");
		Response response = target.request().get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetVnfms_1() {
		WebTarget target = TestUtils.getWebTarget("vnfms");
		Response response = target.request().get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetVnfms_2() {
		WebTarget target = TestUtils.getWebTarget("vnfms");
		WebTarget targetWithQueryParam = target.queryParam("domainId", "domain1")
				.queryParam("vnfmId", "VNFM1")
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
		WebTarget target = TestUtils.getWebTarget("vnfms/duplicateproperties");
		WebTarget targetWithQueryParam = target.queryParam("name", "testVnfm").queryParam("identifier", "testVnfm");
		Invocation.Builder invocationBuilder = targetWithQueryParam.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testDeleteVnfm() {
		WebTarget target = TestUtils.getWebTarget("vnfms/testVnfm");
		Response response = target.request().delete();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}
}
