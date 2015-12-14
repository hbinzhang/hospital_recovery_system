package com.xmc.hospitalrec.rest.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class Util {
	public static Map<String, Object> genPageQueryResult(long total, Object result) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", result);
		map.put("total", total);
		return map;
	}
	
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}
	
	public static String getNowString() {
		Date date=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");  
        return sdf.format(date);
	}

	public static boolean writeInputStream2LocalFile(InputStream inputStream, String path, String fileName) {
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(new File(path, fileName));
			int length = 0;

			byte[] buff = new byte[8192];

			while (-1 != (length = inputStream.read(buff))) {
				outputStream.write(buff, 0, length);
			}
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static WebTarget getWebTarget(String path, String url) {
    	Client client = ClientBuilder.newClient();  
        WebTarget target = client.target(url).path(path);  
        
		return target;
	}
}
