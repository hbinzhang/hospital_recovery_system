package com.ericsson.nfvo.test;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.ericsson.nfvo.model.test.Address;
import com.ericsson.nfvo.model.test.Customer;

import junit.framework.TestCase;

public class CustomerResourceTest extends TestCase {
	@Test
	public void testCreateCustomer() {
		List<Address> addressList = new ArrayList<Address>();
		Customer obj = new Customer();
		obj.setName("Zhangsan");
		obj.setSex("male");
		
		obj.setAddresses(addressList);
		Address address = null;
		
		address = new Address();
		address.setAddressName("haidian");
		address.setCity("Beijing");
		address.setZipCode("100000");
		addressList.add(address);
		
		address = new Address();
		address.setAddressName("Chaoyang");
		address.setCity("Beijing");
		address.setZipCode("100001");
		addressList.add(address);

		address = new Address();
		address.setAddressName("Hexi");
		address.setCity("Tianjin");
		address.setZipCode("200000");
		addressList.add(address);
		

		WebTarget target = TestUtils.getWebTarget("customers");
		Response response = target.request().buildPost(Entity.entity(obj, MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testUpdateCustomer() {
		List<Address> addressList = new ArrayList<Address>();
		Customer obj = new Customer();
		obj.setName("Zhangsan");
		obj.setSex("male");
		
		obj.setAddresses(addressList);
		Address address = null;
		
		address = new Address();
		address.setAddressName("Chaoyang");
		address.setCity("Beijing");
		address.setZipCode("100020");
		addressList.add(address);

		address = new Address();
		address.setAddressName("Hexi");
		address.setCity("Tianjin");
		address.setZipCode("200100");
		addressList.add(address);

		WebTarget target = TestUtils.getWebTarget("customers/Zhangsan");
		Response response = target.request().buildPut(Entity.entity(obj, MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetCustomer() {
		WebTarget target = TestUtils.getWebTarget("customers/Zhangsan");
		Response response = target.request().get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetCustomers_1() {
		WebTarget target = TestUtils.getWebTarget("customers");
		Response response = target.request().get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGetCustomers_2() {
		WebTarget target = TestUtils.getWebTarget("customers");
		WebTarget targetWithQueryParam = target.queryParam("name", "Zhangsan").queryParam("limit", "1")
				.queryParam("offset", "2");
		Invocation.Builder invocationBuilder = targetWithQueryParam.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testDuplicateProperties() {
		WebTarget target = TestUtils.getWebTarget("customers/duplicateproperties");
		WebTarget targetWithQueryParam = target.queryParam("name", "testCustomerName111").queryParam("identifier", "testCustomer");
		Invocation.Builder invocationBuilder = targetWithQueryParam.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testDeleteCustomer() {
		WebTarget target = TestUtils.getWebTarget("customers/Zhangsan");
		Response response = target.request().delete();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}
}
