package com.dnn.namedpipes.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dnn.namedpipes.NamedPipesReceiver;

@Component
public class IPCameraReceiver extends NamedPipesReceiver {

	@Autowired
	DarknetRequester darknet;

	public IPCameraReceiver() {
		super("ip_camera");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void OnMessage(byte[] bytes) {
		// TODO Auto-generated method stub
		darknet.getInputMessageQueue().add(bytes);
	}

}
