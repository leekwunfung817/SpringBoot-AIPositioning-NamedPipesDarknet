package com.dnn.bean;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import lombok.Data;

@Data
public class DarknetResultSet {

	final ArrayList<DarknetResult> resultset;
	final String[] classes;

	private static ArrayList<DarknetResult> readAnnotation(byte[] imgBytes, String annotation, String[] classes)
			throws IOException {
		ArrayList<DarknetResult> results = new ArrayList<DarknetResult>();
		String[] lines = annotation.split("\n");
		for (String line : lines) {
			results.add(new DarknetResult(imgBytes, line, classes));
		}
		return results;
	}

	public DarknetResultSet(byte[] imgBytes, String annotation, String[] classes) throws IOException {
		resultset = readAnnotation(imgBytes, annotation, classes);
		this.classes = classes;
	}

	public DarknetResultSet(ArrayList<DarknetResult> resultset, String[] classes) {
		this.resultset = resultset;
		this.classes = classes;
	}

	public void showResultImage() {
		BufferedImage bufImg = null;
		for (DarknetResult result : resultset) {
			if (bufImg == null)
				bufImg = result.getDemo();
			else
				bufImg = result.getDemo(bufImg);
		}
		if (bufImg == null)
			DarknetResult.showImage(bufImg);
	}

}
