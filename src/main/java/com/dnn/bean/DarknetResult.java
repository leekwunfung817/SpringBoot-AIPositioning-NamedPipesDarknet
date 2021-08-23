package com.dnn.bean;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class DarknetResult {
	private final int id;
	private final double x, y, width, height;
	private final BufferedImage originImage;

	public DarknetResult(byte[] imgBytes, String annotation) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(imgBytes);
		String[] arr = annotation.split(" ");
		originImage = ImageIO.read(bais);
		double totalW = originImage.getWidth();
		double totalH = originImage.getHeight();
		id = Integer.parseInt(arr[0]);
		x = Double.parseDouble(arr[1]) * totalW;
		y = Double.parseDouble(arr[2]) * totalH;
		width = Double.parseDouble(arr[3]) * totalW;
		height = Double.parseDouble(arr[4]) * totalH;
	}

	public DarknetResult(BufferedImage originImage, int id, double x, double y, double width, double height) {
		super();
		this.originImage = originImage;
		this.id = id;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	private static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public BufferedImage getDemo() {
		BufferedImage copy = deepCopy(originImage);
		Graphics2D graph = copy.createGraphics();
		graph.setColor(Color.RED);
		int w = (int) width;
		int h = (int) height;
		double ltX = x - (width / 2);
		double ltY = y - (height / 2);
		graph.draw(new Rectangle((int) ltX, (int) ltY, w, h));
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

	JFrame frame = null;

	public void showImage(BufferedImage img) {
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

}
