package com.dnn.ramdisk.socket.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

import com.dnn.util.Util;

@Slf4j
public class NettyTCPClient extends ChannelInboundHandlerAdapter {

	ChannelFuture cf;

//	@PostConstruct
	public void init(String ip, int port, NettyTCPClient client) {

		try {
			EventLoopGroup bossGroup = new NioEventLoopGroup();

			Bootstrap bs = new Bootstrap();

			bs.group(bossGroup).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel socketChannel) throws Exception {
							// 处理来自服务端的响应信息
							socketChannel.pipeline().addLast(client);
						}
					});

			// 客户端开启
			log.info("NettyTCPClient: Connect to {}:{}", ip, port);
			cf = bs.connect(ip, port).sync();

//		String reqStr = "我是客户端请求1$_";
//
//		// 发送客户端的请求
//		cf.channel().writeAndFlush(Unpooled.copiedBuffer(reqStr.getBytes()));
////      Thread.sleep(300);
//      cf.channel().writeAndFlush(Unpooled.copiedBuffer("我是客户端请求2$_---".getBytes(Constant.charset)));
//      Thread.sleep(300);
//      cf.channel().writeAndFlush(Unpooled.copiedBuffer("我是客户端请求3$_".getBytes(Constant.charset)));

//      Student student = new Student();
//      student.setId(3);
//      student.setName("张三");
//      cf.channel().writeAndFlush(student);

			// 等待直到连接中断
//		cf.channel().closeFuture().sync();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void send(byte[] bytes) {
		log.info("Client send bytes {} bytes. {}", bytes.length, Util.printBytesHeadTail(bytes, false));
		cf.channel().writeAndFlush(Unpooled.copiedBuffer(bytes));
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Close the connection when an exception is raised.
		log.info("Exception: ", cause);
//		cause.printStackTrace();
		ctx.close();
	}
}
