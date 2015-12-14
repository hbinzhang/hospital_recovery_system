package com.ericsson.nfvo.test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.ericsson.nfvo.model.system.Flavor;

import junit.framework.TestCase;

public class FlavorResourceTest extends TestCase {
	@Test
	public void testCreateFlavor() {
		Flavor obj = new Flavor();

		obj.setName("testFlavorName");
		obj.setDescription("test flavor description");
		obj.setDiskCapacity(512);
		obj.setMemCapacity(2048);
		obj.setvCpuCount(16);
		obj.setVolumeCapacity(4096);

		WebTarget target = TestUtils.getWebTarget("flavors");
		Response response = target.request().buildPost(Entity.entity(obj, MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testUpdateFlavor() {
		Flavor obj = new Flavor();

		obj.setName("testFlavorName");
		obj.setDescription("test flavor description 1111");
		obj.setDiskCapacity(5120);
		obj.setMemCapacity(20480);
		obj.setvCpuCount(160);
		obj.setVolumeCapacity(40960);

		WebTarget target = TestUtils.getWebTarget("flavors/testFlavorName");
		Response response = target.request().buildPut(Entity.entity(obj, MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetFlavor() {
		WebTarget target = TestUtils.getWebTarget("flavors/testFlavorName");
		Response response = target.request().get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetFlavors_1() {
		WebTarget target = TestUtils.getWebTarget("flavors");
		Response response = target.request().get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetFlavors_2() {
		WebTarget target = TestUtils.getWebTarget("flavors");
		WebTarget targetWithQueryParam = target.queryParam("name", "testFlavorName")
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
		WebTarget target = TestUtils.getWebTarget("flavors/duplicateproperties");
		WebTarget targetWithQueryParam = target.queryParam("name", "testFlavorName");
		Invocation.Builder invocationBuilder = targetWithQueryParam.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testDeleteFlavor() {
		WebTarget target = TestUtils.getWebTarget("flavors/testFlavorName");
		Response response = target.request().delete();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}
}
