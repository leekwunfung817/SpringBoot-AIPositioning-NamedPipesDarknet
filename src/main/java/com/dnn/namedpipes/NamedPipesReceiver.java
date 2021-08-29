package com.dnn.namedpipes;

import java.io.IOException;
import java.io.RandomAccessFile;

import javax.annotation.PostConstruct;

import org.mortbay.log.Log;

public class NamedPipesReceiver extends Thread {

	RandomAccessFile pipeFrom = null;
	final String name;

//	"ip_camera"
	public NamedPipesReceiver(String name) {
		this.name = name;
		this.start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		reconnect();
		while (true) {
			try {
				OnMessage(readSource());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					Thread.sleep(30);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	void reconnect() {
		while (true) {
			try {
				if (pipeFrom != null)
					pipeFrom.close();
				pipeFrom = new RandomAccessFile("\\\\.\\pipe\\" + name, "rw");
				Log.info("Success connect NamedPipesReceiver.{}", name);
				break;
			} catch (Exception e) {
				Log.warn("NamedPipesReceiver fail to connect [{}] - {}", name, e.getMessage());
//				e.printStackTrace();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	public byte[] readSource() throws IOException {
		byte[] document = new byte[(int) pipeFrom.length()];
		pipeFrom.readFully(document);
		return document;
	}

	public void OnMessage(byte[] bytes) throws IOException {

	}

}
