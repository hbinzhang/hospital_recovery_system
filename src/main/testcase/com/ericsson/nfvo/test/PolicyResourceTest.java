package com.ericsson.nfvo.test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.Test;

import junit.framework.TestCase;

public class PolicyResourceTest extends TestCase {
	@Test
	public void testCreatePolicy() {
		WebTarget target = TestUtils.getMultiPartFeatureWebTarget("policies/vnfms/VNFM1/vnfs/VNF1");

		FileDataBodyPart bodyPart = new FileDataBodyPart("file", new File("D:\\", "policy.xml"));

		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
		formDataMultiPart.field("domainId", "domain1");
		formDataMultiPart.field("operator", "user1");
		formDataMultiPart.field("description", "policy.xml");
		formDataMultiPart.bodyPart(bodyPart);

		String result = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(formDataMultiPart, formDataMultiPart.getMediaType()), String.class);
		System.out.println(result);
	}

	@Test
	public void testUpdatePolicy() {
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("name", "vnf1_policy_new");
		jsonMap.put("version", "v2.0.0");

		WebTarget target = TestUtils.getWebTarget("policies/vnfms/VNFM1/vnfs/VNF1/policyId/1437289373339");
		Response response = target.request().buildPut(Entity.entity(jsonMap, MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGet() {
		WebTarget target = TestUtils.getWebTarget("policies");
		Response response = target.request().get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetPolicys_1() {
		WebTarget target = TestUtils.getWebTarget("policies/vnfms/VNFM1/vnfs/VNF1/policyId/1437289373339");
		Response response = target.request().get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetPolicys_2() {
		WebTarget target = TestUtils.getWebTarget("policies");
		WebTarget targetWithQueryParam = target.queryParam("domainId", "domain1")
				.queryParam("vnfmId", "VNFM2")
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
		WebTarget targetWithQueryParam = target.queryParam("name", "testPolicyName111").queryParam("identifier", "testPolicy");
		Invocation.Builder invocationBuilder = targetWithQueryParam.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testDeletePolicy() {
		WebTarget target = TestUtils.getWebTarget("policies/vnfms/VNFM1/vnfs/VNF1/policyId/1437955066544");
		Response response = target.request().delete();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testActivePolicy() {
		WebTarget target = TestUtils.getWebTarget("policies/vnfms/VNFM2/vnfs/VNF2/policyId/1437289284414/active");
		Response response = target.request().buildPut(Entity.entity("", MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testDeactivePolicy() {
		WebTarget target = TestUtils.getWebTarget("policies/vnfms/VNFM2/vnfs/VNF2/policyId/1437289284414/deactive");
		Response response = target.request().buildPut(Entity.entity("", MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testViewContent() {
		WebTarget target = TestUtils.getWebTarget("policies/vnfms/VNFM2/vnfs/VNF2/policyId/1437289284414/content");
		Response response = target.request().get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}
}
