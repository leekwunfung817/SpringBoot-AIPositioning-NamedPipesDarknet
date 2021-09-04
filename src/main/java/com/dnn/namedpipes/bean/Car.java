package com.dnn.namedpipes.bean;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import lombok.Data;

@Data
public class Car {
	String carType;
	BufferedImage carImg;
	ArrayList<String> licensePlantList;
}
