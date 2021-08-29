package com.dnn.namedpipes.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dnn.bean.DarknetOCRString;
import com.dnn.bean.DarknetResult;
import com.dnn.bean.DarknetResultSet;
import com.dnn.namedpipes.controller.IPCameraReceiver.CallBack;

@Component
public class AIController extends Thread {

	@Autowired
	DarknetRequester darknet;
	@Autowired
	IPCameraReceiver ipcam;

	public static final String CTR = "ctr";
	public static final String OCR = "ocr";
	public static final String LPR = "lpr";

	@PostConstruct
	public void init() throws IOException {
		darknet.addDarkNet(CTR);
		darknet.addDarkNet(LPR);
		darknet.addDarkNet(OCR);
		ipcam.addCamera("192.168.169.117", new CallBack() {
			@Override
			public void OnReturn(String unit, byte[] bytes) throws IOException {
				// TODO Auto-generated method stub
				DarknetResultSet carTypeSet = darknet.predict(CTR, bytes);

				for (DarknetResult car : carTypeSet.getResultset()) {
					BufferedImage carImg = car.getCut();
					byte[] carImgBytes = DarknetResult.convert(carImg);
					DarknetResultSet licentPlantSet = darknet.predict(LPR, carImgBytes);
					ArrayList<String> licensePlantList = new ArrayList<String>();
					for (DarknetResult licentPlant : licentPlantSet.getResultset()) {
						try {
							byte[] cutBytes = DarknetResult.convert(licentPlant.getCut());
							DarknetResultSet characterSet = darknet.predict(OCR, cutBytes);
							String licensePlantString = DarknetOCRString.toOcrString(characterSet);
							licensePlantList.add(licensePlantString);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					OnCar(unit, carImg, car.getName(), licensePlantList);
				}
			}
		});
	}

	@Override
	public void run() {

	}

	public void OnCar(String camera, BufferedImage carImg, String carName, ArrayList<String> licensePlantList) {

	}

}
