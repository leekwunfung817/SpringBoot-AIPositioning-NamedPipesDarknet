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
import com.dnn.namedpipes.bean.Car;
import com.dnn.namedpipes.controller.IPCameraReceiver.CallBack;
import com.dnn.util.Util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AIController extends Thread {

	@Autowired
	Util util;

	@Autowired
	IPCameraReceiver ipcam;

	@Autowired
	DarknetRequester darknet;

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
			public void OnReturn(String unit, byte[] bytes) {
				// TODO Auto-generated method stub
				detectWhole(unit, bytes);
			}
		});
	}

	@Override
	public void run() {

	}

	public void detectWhole(String unit, byte[] imgBytes) {
		DarknetResultSet carTypeSet = darknet.predict(CTR, imgBytes);
		for (DarknetResult car : carTypeSet.getResultset()) {
			BufferedImage carImg = car.getCut();
			try {
				byte[] carImgBytes = DarknetResult.convert(carImg);
				ArrayList<String> licensePlantList = detectLicensePlant(carImgBytes);
				onCar(unit, carImg, car.getName(), licensePlantList);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public ArrayList<String> detectLicensePlant(byte[] carImgBytes) {
		DarknetResultSet licentPlantSet = darknet.predict(LPR, carImgBytes);
		ArrayList<String> licensePlantList = new ArrayList<String>();
		for (DarknetResult licentPlant : licentPlantSet.getResultset()) {
			try {
				BufferedImage licensePlantImg = licentPlant.getCut();
				byte[] cutBytes = DarknetResult.convert(licensePlantImg);
				DarknetResultSet characterSet = darknet.predict(OCR, cutBytes);
				String licensePlantString = DarknetOCRString.toOcrString(characterSet);
				licensePlantList.add(licensePlantString);
				onLicensePlant(licensePlantImg, cutBytes, licensePlantString);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return licensePlantList;
	}

	public void onLicensePlant(BufferedImage licensePlantImg, byte[] cutBytes, String licensePlantString) {

	}

	public void onCar(String cameraIP, BufferedImage carImg, String carType, ArrayList<String> licensePlantList) {
		log.info("Camera IP {} Car type {} License Plant {}", cameraIP, carType, licensePlantList);
		Car carObj = new Car();
		carObj.setCarImg(carImg);
		carObj.setCarType(carType);
		carObj.setLicensePlantList(licensePlantList);
		onCar(carObj);
	}

	public void onCar(Car car) {

	}

}
