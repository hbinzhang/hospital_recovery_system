package com.ericsson.nfvo.test;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.Test;

import junit.framework.TestCase;

public class ModuleResourceTest extends TestCase {

	@Test
	public void testGetTreeModules() {
		WebTarget target = TestUtils.getWebTarget("treemodules");
		Response response = target.request().get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}
}
