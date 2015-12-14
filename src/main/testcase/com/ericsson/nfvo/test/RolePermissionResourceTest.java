package com.ericsson.nfvo.test;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.ericsson.nfvo.model.system.RolePermission;
import com.ericsson.nfvo.model.system.RolePermissionPK;

import junit.framework.TestCase;

public class RolePermissionResourceTest extends TestCase {

	@Test
	public void testSetRolePermissions_2() {
		List<RolePermission> rolePermissionList = new ArrayList<RolePermission>();
		RolePermission rp = null;

		rp = new RolePermission();
		rp.setPermissionValue(1);
		rp.setIdentifier(new RolePermissionPK(2, "system_domain"));
		rolePermissionList.add(rp);

		rp = new RolePermission();
		rp.setPermissionValue(1);
		rp.setIdentifier(new RolePermissionPK(2, "system_user"));
		rolePermissionList.add(rp);

		rp = new RolePermission();
		rp.setPermissionValue(1);
		rp.setIdentifier(new RolePermissionPK(2, "catalog_vnf"));
		rolePermissionList.add(rp);

		WebTarget target = TestUtils.getWebTarget("rolepermissions/2");
		Response response = target.request().buildPut(Entity.entity(rolePermissionList, MediaType.APPLICATION_JSON)).invoke();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

	@Test
	public void testGet1() {
		WebTarget target = TestUtils.getWebTarget("rolepermissions/1");
		Response response = target.request().get();
		System.out.println("status=" + response.getStatus());
		System.out.println(response.readEntity(String.class));
		response.close();
	}

}
