package com.dnn.namedpipes;

import java.io.IOException;
import java.io.RandomAccessFile;

import javax.annotation.PostConstruct;

public class NamedPipesReceiver {

	RandomAccessFile pipeFrom = null;
	final String name;

//	"ip_camera"
	public NamedPipesReceiver(String name) {
		this.name = name;
		while (true) {
			reconnect();
			try {
				while (true) {
					OnMessage(readSource());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	void reconnect() {
		try {
			if (pipeFrom != null)
				pipeFrom.close();
			pipeFrom = new RandomAccessFile("\\\\.\\pipe\\" + name, "rw");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public byte[] readSource() throws IOException {
		byte[] document = new byte[(int) pipeFrom.length()];
		pipeFrom.readFully(document);
		return document;
	}

	public void OnMessage(byte[] bytes) {

	}

}
