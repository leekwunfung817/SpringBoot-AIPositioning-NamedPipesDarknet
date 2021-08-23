package com.dnn.ramdisk.socket.netty.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import io.netty.channel.ChannelHandler;
import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dnn.config.TcpServerSocketConfiguration;
import com.dnn.ramdisk.ANNRamdiskClient;
import com.dnn.ramdisk.ANNRamdiskClient.PredictBean;
import com.dnn.ramdisk.socket.netty.NettyTCPServer;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
@Component("JpgPredictServer")
public class JpgPredictServer extends NettyTCPServer {

	@Autowired
	TcpServerSocketConfiguration config;

	@PostConstruct
	public void postConstruct() {
		init(config.getTcpJpgPort(), new JpgPredictServer());
	}

	@Override
	public void OnJpg(ChannelHandlerContext ctx, BufferedImage img) {
		// TODO Auto-generated method stub
		log.info("OnJpg received");
		PredictBean bean = new PredictBean() {
			@Override
			public void OnResponse(byte[] byteOut) {
				// TODO Auto-generated method stub
				if (byteOut != null) {
					Log.info("Return {} bytes", byteOut.length);
					ctx.writeAndFlush(Unpooled.copiedBuffer(byteOut));
				}
			}
		};
		bean.setImg(img);
		ANNRamdiskClient.predict(bean);
	}

}
