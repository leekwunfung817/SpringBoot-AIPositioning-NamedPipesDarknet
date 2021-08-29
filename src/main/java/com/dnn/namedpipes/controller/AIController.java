package com.dnn.namedpipes.controller;

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
			public void OnReturn(byte[] bytes) {
				// TODO Auto-generated method stub
				DarknetResultSet carTypeSet = darknet.predict(CTR, bytes);
				DarknetResultSet licentPlantSet = darknet.predict(LPR, bytes);
				for (DarknetResult licentPlant : licentPlantSet.getResultset()) {
					try {
						byte[] cutBytes = DarknetResult.convert(licentPlant.getCut());
						DarknetResultSet characterSet = darknet.predict(OCR, cutBytes);
						String licensePlantString = DarknetOCRString.toOcrString(characterSet);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public void run() {

	}

}
