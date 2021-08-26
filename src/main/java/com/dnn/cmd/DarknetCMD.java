package com.dnn.cmd;

import java.io.*;

import javax.annotation.PostConstruct;

import org.mortbay.log.Log;
import org.springframework.stereotype.Component;

import lombok.Getter;

public class DarknetCMD extends Thread {

	final String name;

	public DarknetCMD(String name) {
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
						"cd darknet_resources && " //
								+ "\"../darknet_release/darknet.exe\" detector " //
								+ "test_detector_named_pipes_service " //
								+ name + ".config.data " //
								+ name + ".cfg " //
								+ name + ".weights " //
								+ "-thresh 0.01 " //
								+ "-dont_show -save_labels " //
								+ "-ext_output 11_1_i_h__20190621093202_27871908.jpg " //
								+ "-pipe_name " + name + " ");
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
					Log.info("DarknetCMD:{}", line);
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