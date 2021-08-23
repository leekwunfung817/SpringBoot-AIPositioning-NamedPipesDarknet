package com.dnn.ramdisk;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@DependsOn("JpgPredictServer")
public class ANNRamdiskClient extends Thread {

	File jpg = new File("R:\\predict.jpg");
	File permit = new File("R:\\predict.per");
	File completed = new File("R:\\predict_result.com");
	File result = new File("R:\\predict_result.txt");

	static ConcurrentLinkedQueue<PredictBean> queue = new ConcurrentLinkedQueue<PredictBean>();

	@Data
	public static class PredictBean {
		BufferedImage img;

		public void OnResponse(byte[] byteOut) {

		}
	}

	@PostConstruct
	public void init() throws IOException {
		log.info("Create randisk client.");
		this.start();
	}

	int l2 = Byte.toUnsignedInt((byte) 0xff);

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		while (true) {
			try {
				if (queue.size() > 0) {
					log.info("DeQueue");
					for (PredictBean bean : queue) {
//						long startTime = System.nanoTime();
						String result = predict(bean.getImg());
						bean.OnResponse(result.getBytes());
//					log.info("Result:{}", result);
//						DataOutputStream dOut = new DataOutputStream(bean.getSendbackSocket().getOutputStream());
//						byte[] byteOut = result.getBytes();
//						for (byte byt : byteOut) {
//							if (l2 == byt) {
//								log.info("Invalid char");
//							}
//							dOut.write(byt);
//						}
//						dOut.write(0xff);
//						dOut.flush();
//						dOut.close();
//						log.info("return predict duraiton: {}", (System.nanoTime() - startTime) / 1000000);
					}
					queue.clear();
				}
				Thread.sleep(3);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void predict(PredictBean bean) {
//		PredictBean bean = new PredictBean();
//		bean.setImg(img);
//		bean.setSendbackSocket(sendbackSocket);
		log.info("Append queue");
		queue.add(bean);
	}

	public synchronized String predict(BufferedImage img) {
		try {

			long startTime = System.nanoTime();
			ImageIO.write(img, "jpg", jpg);
//			log.info("Write image");
			if (!jpg.exists())
				return null;
//			log.info("Create new File");
			permit.createNewFile();
//			if (!permit.exists()) {
//				return null;
//			}
//			log.info("Begin predict");
			while (!completed.exists()) {
				Thread.sleep(3);
//				log.info("Predict not complete yet");
			}
//			if (!result.exists()) {
//				return null;
//			}
			String resultStr = FileUtils.readFileToString(result);
			completed.delete();
			result.delete();

			log.info("Ramdisk predict duraiton: {}", (System.nanoTime() - startTime) / 1000000);
			return resultStr;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
