package com.dnn.ramdisk.socket.netty;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dnn.util.Util;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyTCPServer extends ChannelInboundHandlerAdapter {

	ServerBootstrap serverBootstrap;
	ChannelFuture channelFuture;

	public void init(int port, NettyTCPServer server) {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(group);
			serverBootstrap.channel(NioServerSocketChannel.class);
			serverBootstrap.localAddress(new InetSocketAddress("localhost", port));
			serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
				protected void initChannel(SocketChannel socketChannel) throws Exception {
					socketChannel.pipeline().addLast(server);
				}
			});
			channelFuture = serverBootstrap.bind().sync();
			log.info("NettyTCPServer: Listen to {}", port);
//			channelFuture.channel().closeFuture().sync();
//			group.shutdownGracefully().sync();
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}

	HashMap<String, byte[]> arr = new HashMap<String, byte[]>();

	private byte[] appendBytes(ChannelHandlerContext ctx, byte[] newBs) {
		String id = ctx.channel().id().toString();
		if (arr.get(id) != null) {
			byte[] lastB = arr.get(id);
			arr.put(id, concat(lastB, newBs));
		} else {
			arr.put(id, newBs);
		}
		return arr.get(id);
	}

	private byte[] concat(byte[] a, byte[] b) {
//		Log.info("concat from {}<>{}", Util.printBytesHeadTail(a, false), Util.printBytesHeadTail(b, false));
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
//		Log.info("concat to {}", Util.printBytesHeadTail(c, false));
		return c;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//		super.channelRead(ctx, msg);
		ByteBuf buf = (ByteBuf) msg;
//		log.info("Server Readable {}", buf.readableBytes());
		byte[] bytes = new byte[buf.readableBytes()];
		buf.readBytes(bytes);
		byte[] ac_bytes = appendBytes(ctx, bytes);

		String outLog = Util.printBytesHeadTail(ac_bytes, false);
//		Log.info("NettyTCPServer Receive " + ac_bytes.length + " bytes from channel {}. {}",
//				ctx.channel().id().toString(), outLog);
//		System.out.println("Message receive " + outLog);
		if (Util.isJpg(bytes)) {
			Log.info("NettyTCPServer Receive Image. {}", Util.printBytesHeadTail(ac_bytes, false));
			try {
				BufferedImage newBi = ImageIO.read(new ByteArrayInputStream(ac_bytes));
				if (newBi != null) {
//						Log.info("NettyTCPServer Receive Image 2");
					OnJpg(ctx, newBi);
				} else {
					Log.info("NettyTCPServer Receive Wrong format image. hash {}", Arrays.hashCode(bytes));
				}
			} catch (Exception e) {
				log.warn("Exception:", e);
			}
		} else {
			OnMessage(ctx, bytes);
		}
//			if (bytes != null) {
//				Log.info("Return {} bytes", bytes.length);
//				ctx.writeAndFlush(Unpooled.copiedBuffer(bytes));
//			}
	}

	public void OnMessage(ChannelHandlerContext ctx, byte[] bytes) throws IOException {

	}

	public void OnJpg(ChannelHandlerContext ctx, BufferedImage img) {
		Log.info("Default OnJpg");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}

}