package com.dnn.namedpipes.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dnn.cmd.CloudIPCameraCMD;
import com.dnn.cmd.DarknetCMD;
import com.dnn.namedpipes.NamedPipesReceiver;
import com.dnn.namedpipes.NamedPipesRequester;
import com.sun.jna.Callback;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class IPCameraReceiver {

	@Autowired
	DarknetRequester darknet;

	private HashMap<String, NamedPipesReceiver> listCloudIPCameraRequester = new HashMap<String, NamedPipesReceiver>();
	private HashMap<String, CloudIPCameraCMD> listCloudIPCameraCMD = new HashMap<String, CloudIPCameraCMD>();

	public void addCamera(String ipaddress, CallBack callback) {
		listCloudIPCameraRequester.put(ipaddress, new NamedPipesReceiver(ipaddress) {
			@Override
			public void OnMessage(byte[] bytes) throws IOException {
				// TODO Auto-generated method stub
//				darknet.predict("ctr", bytes);
				callback.OnReturn(bytes);
			}
		});
		listCloudIPCameraCMD.put(ipaddress, new CloudIPCameraCMD(ipaddress));
	}

	public static class CallBack {
		public void OnReturn(byte[] bytes) throws IOException {

		}

		public void OnReturn(String unit, byte[] bytes) throws IOException {

		}
	}

	@PreDestroy
	public void preDistroy() {
		for (Map.Entry<String, CloudIPCameraCMD> entry : listCloudIPCameraCMD.entrySet()) {
			log.info("IPCameraReceiverController preDistroy:" + entry.getKey() + "/" + entry.getValue());
			Process process = entry.getValue().getProcess();
			while (process.isAlive()) {
				process.destroy();
			}
		}
	}
}
