package com.dnn.ramdisk.socket.netty.impl;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.dnn.config.TcpServerSocketConfiguration;
import com.dnn.ramdisk.socket.netty.NettyTCPClient;
import com.dnn.util.Util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Component
@DependsOn("JpgPredictServer")
public class JpgPredictClient extends NettyTCPClient {

	BufferedImage img = null;

	@Getter
	File imgFile = new File("a.jpg");

	@Autowired
	TcpServerSocketConfiguration config;

	@PostConstruct
	public void postConstruct() {
		init(config.getIp(), config.getTcpJpgPort(), new JpgPredictClient() {
			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				// TODO Auto-generated method stub
				ByteBuf buf = (ByteBuf) msg;
				log.info("Server Readable {}", buf.readableBytes());
				byte[] bytes = new byte[buf.readableBytes()];
				buf.readBytes(bytes);
				OnMessage(bytes);
//				super.channelRead(ctx, msg);
			}
		});
	}

//	@Scheduled(fixedDelay = 3000)
//	public void keepAlive() throws IOException {
//		send(getImgBytes());
//	}

	public void OnMessage(byte[] bytes) {
		// TODO Auto-generated method stub
		String txt = new String(bytes);
		log.info("Client Received:: {}", txt);
		if (img != null) {
			ArrayList<PredictResult> arr = PredictResult.getList(txt, img);
			for (PredictResult result : arr) {
				result.showImage();
			}
		}

	}

	public BufferedImage getImgObj() throws IOException {
		img = ImageIO.read(imgFile);
		return img;
	}

	public byte[] getImgBytes() throws IOException {
		byte[] bytes = FileUtils.readFileToByteArray(imgFile);
//		log.info("Image size {} hash {}", bytes.length, Arrays.hashCode(bytes));
		return bytes;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// TODO Auto-generated method stub
		super.exceptionCaught(ctx, cause);
		postConstruct();
	}

	static class PredictResult {
		HashMap<String, String> datas = new HashMap<String, String>();

		public static ArrayList<PredictResult> getList(String whole, BufferedImage img) {
			ArrayList<PredictResult> arr = new ArrayList<PredictResult>();
			String[] lines = whole.split("\n");
			for (String line : lines) {
				if (line.length() > 10) {
					arr.add(new PredictResult(line, deepCopy(img)));
				}
			}
			return arr;
		}

		BufferedImage img;

		public PredictResult(String result, BufferedImage img) {

			this.img = img;
			String[] element = result.split(",");
			for (String line : element) {
				String[] arr = line.split(":");
				datas.put(arr[0], arr[1]);
			}
			String classID = datas.get("Class-ID");

			boolean isValid = classID != null;
			isValid &= datas.get("X") != null;
			isValid &= datas.get("Y") != null;
			isValid &= datas.get("W") != null;
			isValid &= datas.get("H") != null;
			isValid &= datas.get("PROB") != null;
			if (!isValid) {
				log.info("Invalid prediction annotation.");
				return;
			}

			int x = (int) (img.getWidth() * Double.parseDouble(datas.get("X")));
			int y = (int) (img.getHeight() * Double.parseDouble(datas.get("Y")));
			int w = (int) (img.getWidth() * Double.parseDouble(datas.get("W")));
			int h = (int) (img.getHeight() * Double.parseDouble(datas.get("H")));
			int prob = (int) (100 * Double.parseDouble(datas.get("PROB")));

			Graphics2D graph = img.createGraphics();
			graph.setColor(Color.RED);
			graph.draw(new Rectangle(x, y, w, h));
			graph.dispose();
		}

		static BufferedImage deepCopy(BufferedImage bi) {
			ColorModel cm = bi.getColorModel();
			boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
			WritableRaster raster = bi.copyData(null);
			return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
		}

		public void showImage() {
			Util.showResultImage(img);
		}
	}
//	public void 

}
