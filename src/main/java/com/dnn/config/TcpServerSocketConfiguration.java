package com.dnn.config;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Data
@Configuration
public class TcpServerSocketConfiguration {

	@Value("${ip:127.0.0.1}")
	private String ip;

	@Value("${port:6847}")
	private int tcpJpgPort;
	@Value("${port:6947}")
	private int tcpStringPort;

}