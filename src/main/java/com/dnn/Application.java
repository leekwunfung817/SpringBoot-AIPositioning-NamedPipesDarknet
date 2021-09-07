package com.dnn;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.dnn.ramdisk.ANNRamdiskClient;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class Application implements CommandLineRunner {

	@Autowired
	ANNRamdiskClient adapter;

	public static void main(String[] args) {
		log.info("Version:{}", System.getProperty("java.version"));
		SpringApplicationBuilder builder = new SpringApplicationBuilder(Application.class);
		builder.headless(false);
		ConfigurableApplicationContext context = builder.run(args);
	}

	@Override
	public void run(String... args) throws Exception {

	}
}
