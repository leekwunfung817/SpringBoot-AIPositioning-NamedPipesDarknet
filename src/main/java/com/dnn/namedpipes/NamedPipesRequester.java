package com.dnn.namedpipes;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dnn.bean.DarknetResult;
import com.dnn.util.Util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NamedPipesRequester {

	protected RandomAccessFile pipeTo = null;
	protected final String name;

//	darknet_anotation
//	@PostConstruct

	@Getter
	ArrayList<byte[]> inputMessageQueue = new ArrayList<byte[]>();

	public NamedPipesRequester(String name) {
		log.info("Create Name pipes requester {}", name);
		this.name = name;
		while (true) {
			reconnect();
			try {
				while (true) {
					if (inputMessageQueue.size() > 0) {
						for (byte[] msg : inputMessageQueue) {
							sendDestination(msg);
						}
					} else {
						Thread.sleep(100);
					}
				}
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		try {
//			// Connect to the pipe
//			pipe = new RandomAccessFile("\\\\.\\pipe\\darknet_anotation", "rw");
////			String echoText = "Hello word\n";
//			// write to pipe
////			pipe.write(echoText.getBytes());
//			// read response
////			String echoResponse = pipe.readLine();
////			System.out.println("Response: " + echoResponse);
////			pipe.close();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	public void reconnect() {
		while (true) {
			try {
				if (pipeTo != null)
					pipeTo.close();
				log.info("Reconnect {}", name);
				pipeTo = new RandomAccessFile("\\\\.\\pipe\\" + name, "rw");
				log.info("Named pipes connect success. {}", name);
				keepAlive();
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

	public void sendDestination(byte[] bytes) throws IOException {
		pipeTo.write(bytes);
	}

//	@Scheduled(fixedDelay = 1000, initialDelay = 1000)
	public void keepAlive() throws IOException {
		try {
			log.info("keepAlive sendBytes {}", name);
			byte[] bytes = Util.getSampleImgBytes();
			pipeTo.write(bytes);
			String echoResponse = pipeTo.readLine();
			log.info("keepAlive Response {}", echoResponse);
//			System.out.println("Response: " + echoResponse);
			DarknetResult result = new DarknetResult(bytes, echoResponse);
			result.showResultImage();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			reconnect();
		}
	}
}
