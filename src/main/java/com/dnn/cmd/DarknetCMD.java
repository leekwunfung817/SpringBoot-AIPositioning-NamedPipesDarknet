package com.dnn.cmd;

import java.io.*;

import javax.annotation.PostConstruct;

import org.mortbay.log.Log;
import org.springframework.stereotype.Component;

@Component
public class DarknetCMD extends Thread {

	@PostConstruct
	public void init() throws Exception {
		this.start();
//		Thread.sleep(10000);
	}

	@Override
	public void run() {
		try {
			// TODO Auto-generated method stub
			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", //
					"cd darknet_resources && " //
							+ "\"../darknet_release/darknet.exe\" detector " //
							+ "test_detector_named_pipes_service " //
							+ "ctr.config.data " //
							+ "ctr.cfg " //
							+ "ctr.weights " //
							+ "-thresh 0.01 " //
							+ "-dont_show -save_labels " //
							+ "-ext_output 11_1_i_h__20190621093202_27871908.jpg");
			builder.redirectErrorStream(true);
			Process p = builder.start();
			BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while (true) {
				line = r.readLine();
				if (line == null) {
					Log.info("Read Null");
					continue;
				}
				System.out.println(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}