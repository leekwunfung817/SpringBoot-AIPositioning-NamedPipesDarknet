package com.dnn.namedpipes.controller;

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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class IPCameraReceiverController {

	@Autowired
	DarknetRequesterController darknet;

	private HashMap<String, NamedPipesReceiver> listCloudIPCameraRequester = new HashMap<String, NamedPipesReceiver>();
	private HashMap<String, CloudIPCameraCMD> listCloudIPCameraCMD = new HashMap<String, CloudIPCameraCMD>();

	public IPCameraReceiverController() {
		// TODO Auto-generated constructor stub
		add("192.168.169.117");
	}

	public void add(String ipaddress) {
		listCloudIPCameraRequester.put(ipaddress, new NamedPipesReceiver(ipaddress) {
			@Override
			public void OnMessage(byte[] bytes) {
				// TODO Auto-generated method stub
				darknet.predict("ctr", bytes);
			}
		});
		listCloudIPCameraCMD.put(ipaddress, new CloudIPCameraCMD(ipaddress));
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
