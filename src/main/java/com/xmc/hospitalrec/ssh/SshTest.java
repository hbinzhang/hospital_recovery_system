package com.xmc.hospitalrec.ssh;

import static com.jcraft.jsch.ChannelSftp.OVERWRITE;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.xmc.hospitalrec.rest.br.BrResource;

public class SshTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(SshTest.class);
	
	private JSch jsch = new JSch();
	private Session session = null;
	private Channel channel = null;

	public boolean connect(String ip, int port, String userName, String password) {
		try {
			// jsch.addIdentity("~/.ssh/nfvo");
			session = jsch.getSession(userName, ip, port);
			if (password != null && !password.equals(""))
				session.setPassword(password);
			// StrictHostKeyChecking: ask | yes | no
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config); // 为Session对象设置properties
			session.setTimeout(3000); // 设置timeout时间
			session.connect(); // 通过Session建立链接
			System.out.println("connect succ.");
			return true;
		} catch (JSchException e) {
			e.printStackTrace();
			return false;
		}
	}

	protected boolean openShell() {
		try {
			channel = session.openChannel("shell");
			((ChannelShell) channel).setPtyType("vt102");
			((ChannelShell) channel).setEnv("LANG", "zh_CN.UTF-8");
			channel.connect(3 * 1000);
		} catch (JSchException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("shell channel connect succ.");
		return true;
	}

	protected boolean openSftp() {
		try {
			channel = session.openChannel("sftp");
			channel.connect();
		} catch (JSchException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("sftp channel connect succ.");
		return true;
	}

	public void testPutFile() {
		ChannelSftp sftp = (ChannelSftp) channel;
		String src = "d:/maven.txt";
		String dst = "maven.txt";
		try {
			sftp.put(src, dst, OVERWRITE);
			System.out.println("put file succ.");
		} catch (SftpException e) {
			e.printStackTrace();
		}
	}

	public void execShell(String command) {
		try {
			channel = session.openChannel("shell");
			ChannelShell shellChannel = (ChannelShell) channel;
			shellChannel.setPtyType("vt102");
			shellChannel.setEnv("LANG", "zh_CN.UTF-8");
			shellChannel.connect(3 * 1000);
			InputStream instream = shellChannel.getInputStream();
			OutputStream outstream = shellChannel.getOutputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(instream));

			String shellCommand = command + "\n";
			outstream.write(shellCommand.getBytes());
			outstream.flush();
			while (shellChannel.isConnected()) {
				System.out.println("+++" + instream.available());
				String line = reader.readLine();
				// byte[] data = new byte[instream.available()];
				// int nLen = instream.read(data);
				// if (nLen < 0) {
				// throw new Exception("network error.");
				// }
				//
				// String temp = new String(data, 0, nLen, "utf-8");
				System.out.println(line);
				// System.out.println(shellChannel.getExitStatus());
				if (line == null)
					break;
			}

			outstream.close();
			instream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testExec(String command) {
		try {
			channel = session.openChannel("exec");
			ChannelExec shellChannel = (ChannelExec) channel;
			shellChannel.setCommand(command);
			shellChannel.connect(3 * 1000);

			InputStream instream = shellChannel.getInputStream();
			byte[] tmp = new byte[1024];
			while (true) {
				System.out.println("+++" + instream.available());
				while (instream.available() > 0) {
					int i = instream.read(tmp, 0, 1024);
					if (i < 0)
						break;
					System.out.print(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					if (instream.available() > 0)
						continue;
					System.out.println("exit-status: " + channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			instream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public String execCmd(String command) {
		try {
			LOGGER.info("[execCmd] start " + command);
			channel = session.openChannel("exec");
			ChannelExec shellChannel = (ChannelExec) channel;
			shellChannel.setCommand(command);
			// shellChannel.setPtyType("vt102");
			// shellChannel.setEnv("LANG", "zh_CN.UTF-8");
			shellChannel.setInputStream(null);
			shellChannel.setErrStream(System.err);
			shellChannel.connect(3 * 1000);

			InputStream instream = shellChannel.getInputStream();
			byte[] tmp = new byte[1024];
			StringBuilder sb = new StringBuilder();
			for (int k = 0; k < 40; k++) {// At most fetch 100 times
				LOGGER.info("get one output avai size: " + instream.available());
				while (instream.available() > 0) {
					int i = instream.read(tmp, 0, 1024);
					if (i < 0) {
						break;
					}
					String oneResult = new String(tmp, 0, i);
					sb.append(oneResult);
				}
				if (channel.isClosed()) {
					if (instream.available() > 0)
						continue;
					LOGGER.debug("exit-status: " + channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(250);
				} catch (Exception ee) {
				}
			}
			instream.close();
			LOGGER.info("[execCmd] return: " + sb.toString());
			return sb.toString();
		} catch (Exception ex) {
			LOGGER.error("[execCmd] exe error", ex);
			return null;
		}
	}
	
	public void disconnect() {
		if (channel != null) {
			channel.disconnect();
			channel = null;
		}
		if (session != null) {
			session.disconnect();
			session = null;
		}
	}

	public static void main(String[] args) {
		SshTest test = new SshTest();
		test.test();
	}

	public void test() {
		System.out.println("connect...");
		if (!connect("10.3.10.106", 22, "root", "private"))
			return;
		// if(!openShell())
		//execShell("ls");
		execCmd("ls");
		// if(!openSftp())
		// return;
		// this.testPutFile();
		disconnect();
	}

}
