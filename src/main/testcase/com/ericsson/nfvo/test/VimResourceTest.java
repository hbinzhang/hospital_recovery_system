package com.ericsson.nfvo.test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.ericsson.nfvo.model.system.Vim;

import junit.framework.TestCase;

public class VimResourceTest extends TestCase {
	@Test
	public void testCreateVim() {
		Vim obj = new Vim();

		obj.setIdentifier("testVim");
		obj.setName("testVimName");
		obj.setDescription("test vim description");
		obj.setDomainId("domain1");
		obj.setServerURL("http://localhost:8080/vfvo/rest/v1");
		obj.setPassword("666666");
		obj.setUserName("user1");
		obj.setVendor("ericsson");
		obj.setVersion("v2.0.0");
		obj.setVnfmId("VNFM1");

		WebTarget target = TestUtils.getWebTarget("vims");
		Response response = target.request().buildPost(Entity.entity(obj, MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testUpdateVim() {
		Vim obj = new Vim();

		obj.setIdentifier("testVim");
		obj.setName("testVimName");
		obj.setDescription("test vim description");
		obj.setDomainId("domain1");
		obj.setServerURL("http://localhost:8080/vfvo/rest/v1");
		obj.setPassword("888888");
		obj.setUserName("user2");
		obj.setVendor("ericsson");
		obj.setVersion("v2.0.1");
		obj.setVnfmId("VNFM1");

		WebTarget target = TestUtils.getWebTarget("vims/testVim");
		Response response = target.request().buildPut(Entity.entity(obj, MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetVim() {
		WebTarget target = TestUtils.getWebTarget("vims/testVim");
		Response response = target.request().get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetVims_1() {
		WebTarget target = TestUtils.getWebTarget("vims");
		Response response = target.request().get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetVims_2() {
		WebTarget target = TestUtils.getWebTarget("vims");
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
		WebTarget target = TestUtils.getWebTarget("vims/duplicateproperties");
		WebTarget targetWithQueryParam = target.queryParam("name", "testVim").queryParam("identifier", "testVim");
		Invocation.Builder invocationBuilder = targetWithQueryParam.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testDeleteVim() {
		WebTarget target = TestUtils.getWebTarget("vims/testVim");
		Response response = target.request().delete();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetVimCapacity() {
		WebTarget target = TestUtils.getWebTarget("vims/VIM1/capacity");
		Response response = target.request().get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}
}
