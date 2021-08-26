package com.dnn.cmd;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.mortbay.log.Log;

import lombok.Getter;

public class CloudIPCameraCMD extends Thread {

	final String name;

	public CloudIPCameraCMD(String name) {
		this.name = name;
		this.start();
	}

	@Getter
	Process process = null;

	@Override
	public void run() {
		while (true) {
			try {
				// TODO Auto-generated method stub
				ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", //
						"cd ip_camera_release && " //
								+ "CloudIPCameraClient.exe " //
								+ name //
								+ " 1 1 ");
				builder.redirectErrorStream(true);
				process = builder.start();
				BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;
				while (true) {
					line = r.readLine();
					if (line == null) {
						Log.info("Read Null");
						break;
					}
					Log.info("CloudIPCameraCMD:{}", line);
				}
				while (process.isAlive()) {
					process.destroy();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
