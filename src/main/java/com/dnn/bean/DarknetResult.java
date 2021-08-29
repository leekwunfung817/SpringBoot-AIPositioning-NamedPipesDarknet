package com.dnn.bean;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import lombok.Data;

@Data
public class DarknetResult {
	private final int id;
	private final String name;
	private final double centerX, centerY, width, height, ltX, ltY;
	private final BufferedImage originImage;

	public DarknetResult(byte[] imgBytes, String annotation, String[] names) throws IOException {
		this(imgBytes, annotation.split(" "), names);
	}

	public DarknetResult(byte[] imgBytes, String[] arr, String[] names) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(imgBytes);
		originImage = ImageIO.read(bais);
		double totalW = originImage.getWidth();
		double totalH = originImage.getHeight();
		id = Integer.parseInt(arr[0]);
		centerX = Double.parseDouble(arr[1]) * totalW;
		centerY = Double.parseDouble(arr[2]) * totalH;
		width = Double.parseDouble(arr[3]) * totalW;
		height = Double.parseDouble(arr[4]) * totalH;

		ltX = centerX - (width / 2);
		ltY = centerY - (height / 2);

		this.name = names[id];
	}

//	public DarknetResult(BufferedImage originImage, int id, double x, double y, double width, double height,
//			String name) {
//		super();
//		this.originImage = originImage;
//		this.id = id;
//		this.centerX = x;
//		this.centerY = y;
//		this.width = width;
//		this.height = height;
//
//		ltX = centerX - (width / 2);
//		ltY = centerY - (height / 2);
//
//		this.name = name;
//	}

	private static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public BufferedImage getDemo() {
		BufferedImage copy = deepCopy(originImage);
		return getDemo(copy);
	}

	public BufferedImage getDemo(BufferedImage copy) {
		Graphics2D graph = copy.createGraphics();
		graph.setColor(Color.RED);
		graph.draw(new Rectangle((int) ltX, (int) ltY, (int) width, (int) height));
		graph.dispose();
		return copy;
	}

	public void showOriginImage() {
		showImage(originImage);
	}

	public void showResultImage() {
		BufferedImage img = getDemo();
		showImage(img);
	}

	static JFrame frame = null;

	public static void showImage(BufferedImage img) {
		if (frame != null) {
			frame.setVisible(false);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // if you want the X
		}
		frame = new JFrame();
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(new JLabel(new ImageIcon(img)));
		frame.pack();
		frame.setVisible(true);
	}

	public BufferedImage getCut() {
		return originImage.getSubimage((int) ltX, (int) ltY, (int) width, (int) height);
	}

	public static byte[] convert(BufferedImage bi) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bi, "jpg", baos);
		byte[] bytes = baos.toByteArray();
		return bytes;
	}
}
