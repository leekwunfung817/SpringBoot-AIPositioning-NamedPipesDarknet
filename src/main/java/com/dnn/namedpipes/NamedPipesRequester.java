package com.dnn.namedpipes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.mortbay.log.Log;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dnn.bean.DarknetResult;
import com.dnn.bean.DarknetResultSet;
import com.dnn.util.Util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NamedPipesRequester extends Thread {

	protected RandomAccessFile pipeTo = null;
	protected final String name;
	protected final String[] classes;

//	darknet_anotation
//	@PostConstruct

	@Getter
	ArrayList<byte[]> inputMessageQueue = new ArrayList<byte[]>();

	public NamedPipesRequester(String name) throws IOException {
		log.info("Create Name pipes requester {}", name);
		this.name = name;
		File classesFile = new File("./darknet_resources/" + name + ".predefined_classes.txt");
		FileInputStream fileInputStream = new FileInputStream(classesFile);
		classes = new String(fileInputStream.readAllBytes()).split("\n");
		start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		reconnect();
		while (true) {
			try {
				while (true) {
					if (inputMessageQueue.size() > 0) {
						for (byte[] msg : inputMessageQueue) {
							predict(msg);
						}
					} else {
						Thread.sleep(30);
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				log.warn("Exception:", e);
				reconnect();
			}
		}
	}

	public void reconnect() {
		while (true) {
			try {
				if (pipeTo != null)
					pipeTo.close();
				log.info("Reconnect {}", name);
				pipeTo = new RandomAccessFile("\\\\.\\pipe\\" + name, "rw");
				Log.info("Success connect NamedPipesRequester.{}", name);
				predict();
				break;
			} catch (IOException e) {
				// TODO Auto-generated catch block
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

//	public void sendDestination(byte[] bytes) throws IOException {
//		pipeTo.write(bytes);
//	}

//	@Scheduled(fixedDelay = 1000, initialDelay = 1000)
	public DarknetResultSet predict() {
		byte[] bytes = Util.getSampleImgBytes();
		return predict(bytes);
	}

	public DarknetResultSet predict(byte[] bytes) {
		return predict(bytes, false);
	}

	public DarknetResultSet predict(byte[] bytes, boolean show) {
		try {
			log.info("keepAlive sendBytes {}", name);
			pipeTo.write(bytes);
			String echoResponse = pipeTo.readLine();
			log.info("keepAlive Response {}", echoResponse);
//			System.out.println("Response: " + echoResponse);
			DarknetResultSet results = new DarknetResultSet(bytes, echoResponse, classes);
			if (show)
				results.showResultImage();
			return results;
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			log.warn("Predict fail.");
			reconnect();
			return null;
		}
	}

}
