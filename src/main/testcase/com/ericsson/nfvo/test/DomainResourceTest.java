package com.ericsson.nfvo.test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.ericsson.nfvo.model.system.Domain;

import junit.framework.TestCase;

public class DomainResourceTest extends TestCase {
	@Test
	public void testCreateDomain() {
		Domain obj = new Domain();

		obj.setIdentifier("testDomain");
		obj.setName("testDomainName");
		obj.setStatus(1);
		obj.setDescription("test domain description");

		WebTarget target = TestUtils.getWebTarget("domains");
		Response response = target.request().buildPost(Entity.entity(obj, MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testUpdateDomain() {
		Domain obj = new Domain();

		obj.setIdentifier("testDomain");
		obj.setName("testDomainName111");
		obj.setStatus(0);
		obj.setDescription("test domain description 111");

		WebTarget target = TestUtils.getWebTarget("domains/testDomain");
		Response response = target.request().buildPut(Entity.entity(obj, MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetDomain() {
		WebTarget target = TestUtils.getWebTarget("domains/testDomain");
		Response response = target.request().get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetDomains_1() {
		WebTarget target = TestUtils.getWebTarget("domains");
		Response response = target.request().get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetDomains_2() {
		WebTarget target = TestUtils.getWebTarget("domains");
		WebTarget targetWithQueryParam = target.queryParam("name", "testDomainName111").queryParam("limit", "1")
				.queryParam("offset", "2");
		Invocation.Builder invocationBuilder = targetWithQueryParam.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testDuplicateProperties() {
		WebTarget target = TestUtils.getWebTarget("domains/duplicateproperties");
		WebTarget targetWithQueryParam = target.queryParam("name", "testDomainName111").queryParam("identifier", "testDomain");
		Invocation.Builder invocationBuilder = targetWithQueryParam.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testDeleteDomain() {
		WebTarget target = TestUtils.getWebTarget("domains/testDomain");
		Response response = target.request().delete();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}
}
