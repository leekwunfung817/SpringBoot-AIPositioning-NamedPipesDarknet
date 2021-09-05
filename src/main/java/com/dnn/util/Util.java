package com.dnn.util;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Util {

	static File imgFile = new File("a.jpg");
	@Getter
	static byte[] imgSampleBytes = getSampleImgBytes();

	public static byte[] getSampleImgBytes() {
		byte[] bytes = null;
		try {
			bytes = FileUtils.readFileToByteArray(imgFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		log.info("Image size {} hash {}", bytes.length, Arrays.hashCode(bytes));
		return bytes;
	}

	public static boolean isJpg(byte[] bytes) {
		int l1 = Byte.toUnsignedInt((byte) 0xd9);
		int l2 = Byte.toUnsignedInt((byte) 0xff);
		int l3 = Byte.toUnsignedInt((byte) 0x7f);
		int size = bytes.length;
		boolean m1 = l1 == Byte.toUnsignedInt(bytes[size - 1]);
		boolean m2 = l2 == Byte.toUnsignedInt(bytes[size - 2]);
		boolean m3 = l3 == Byte.toUnsignedInt(bytes[size - 3]);
		return m1 && m2 && m3;
	}

	public static String printBytesHeadTail(byte[] bytes, boolean print) {
		StringBuilder sb = new StringBuilder();
		sb.append("HEX head ");
		for (int i = 0; i < 3; i++) {
			int b = Byte.toUnsignedInt(bytes[i]);
			sb.append(" 0x");
			sb.append(HEX_ARRAY[b >>> 4]);
			sb.append(HEX_ARRAY[b & 0x0F]);
		}
		sb.append("...");
		for (int i = bytes.length - 3; i < bytes.length; i++) {
			int b = Byte.toUnsignedInt(bytes[i]);
			sb.append(" 0x");
			sb.append(HEX_ARRAY[b >>> 4]);
			sb.append(HEX_ARRAY[b & 0x0F]);
		}
		sb.append(" tail");
		String result = sb.toString();
		if (print)
			log.info(result);
		return result;
	}

	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static void showResultImage(BufferedImage img) {
		JFrame frame = new JFrame();
		frame.setVisible(false);
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(new JLabel(new ImageIcon(img)));
//		frame.getContentPane().add(new JLabel(new ImageIcon(img2)));
//		frame.getContentPane().add(new JLabel(new ImageIcon(img3)));
		frame.pack();
		frame.setVisible(true);
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // if you want the X
		// button to close the app
	}
}
