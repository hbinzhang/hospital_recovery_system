package com.ericsson.nfvo.test;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.ericsson.nfvo.model.system.SysConfig;

import junit.framework.TestCase;

public class SysConfigResourceTest extends TestCase {

	@Test
	public void testUpdate() {
		Map<String, String> paraMap = new HashMap<String, String>();
		
		paraMap.put(SysConfig.KEY_FTP_SERVER_IP, "192.168.1.105");
		paraMap.put(SysConfig.KEY_FTP_SERVER_PASSWORD, "password123");
		paraMap.put(SysConfig.KEY_VIM_POLICY, "round");

		WebTarget target = TestUtils.getWebTarget("sysconfigs");
		Response response = target.request().buildPut(Entity.entity(paraMap, MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}


	@Test
	public void testGet() {
		WebTarget target = TestUtils.getWebTarget("sysconfigs");
		Response response = target.request().get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

}
