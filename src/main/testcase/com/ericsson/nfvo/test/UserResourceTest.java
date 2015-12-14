package com.ericsson.nfvo.test;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.ericsson.nfvo.model.system.User;

import junit.framework.TestCase;

public class UserResourceTest extends TestCase {
	@Test
	public void testCreateUser() {
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("password", "666666");
		jsonMap.put("loginName", "testUser");
		jsonMap.put("userName", "testUserName");
		jsonMap.put("password", "666666");
		jsonMap.put("status", 1);
		jsonMap.put("roleId", 1);
		jsonMap.put("mobile", "13301331333");
		jsonMap.put("email", "abc@163.com");

		WebTarget target = TestUtils.getWebTarget("users");
		Response response = target.request().buildPost(Entity.entity(jsonMap, MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testUpdateUser() {
		User obj = new User("testUser", "666666", "testUserName111");

		obj.setDomainId("domain1");
		obj.setEmail("abc@163.com");

		WebTarget target = TestUtils.getWebTarget("users/testUser");
		Response response = target.request().buildPut(Entity.entity(obj, MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetUser() {
		WebTarget target = TestUtils.getWebTarget("users/testUser");
		Response response = target.request().get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetUsers_1() {
		WebTarget target = TestUtils.getWebTarget("users");
		Response response = target.request().get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetUsers_2() {
		WebTarget target = TestUtils.getWebTarget("users");
		WebTarget targetWithQueryParam = target.queryParam("domainId", "domain1").queryParam("limit", "1")
				.queryParam("offset", "2");
		Invocation.Builder invocationBuilder = targetWithQueryParam.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testDuplicateProperties() {
		WebTarget target = TestUtils.getWebTarget("users/duplicateproperties");
		WebTarget targetWithQueryParam = target.queryParam("loginName", "testUser").queryParam("userName", "admin");
		Invocation.Builder invocationBuilder = targetWithQueryParam.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testLogin() {
		Map<String, String> jsonMap = new HashMap<String, String>();
		jsonMap.put("password", "666666");
		jsonMap.put("loginName", "testUser");

		WebTarget target = TestUtils.getWebTarget("authentication/login");
		Response response = target.request().buildPost(Entity.entity(jsonMap, MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}
	
	@Test
	public void testLogout() {
		WebTarget target = TestUtils.getWebTarget("authentication/logout");
		Response response = target.request().buildPut(Entity.entity("", MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}
	
	@Test
	public void testActiveUser() {
		WebTarget target = TestUtils.getWebTarget("users/testUser/active");
		Response response = target.request().buildPut(Entity.entity("", MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}
	
	@Test
	public void testDeactiveUser() {
		WebTarget target = TestUtils.getWebTarget("users/testUser/deactive");
		Response response = target.request().buildPut(Entity.entity("", MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}
	
	@Test
	public void testChangePassword() {
		Map<String, String> jsonMap = new HashMap<String, String>();
		jsonMap.put("password", "666666");
		jsonMap.put("newPassword", "888888");

		WebTarget target = TestUtils.getWebTarget("users/testUser/passwordmodification");
		Response response = target.request().buildPut(Entity.entity(jsonMap, MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testDeleteUser() {
		WebTarget target = TestUtils.getWebTarget("users/testUser");
		Response response = target.request().delete();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}
}
