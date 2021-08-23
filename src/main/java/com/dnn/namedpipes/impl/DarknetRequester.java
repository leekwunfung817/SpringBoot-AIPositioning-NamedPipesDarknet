package com.dnn.namedpipes.impl;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dnn.bean.DarknetResult;
import com.dnn.namedpipes.NamedPipesRequester;
import com.dnn.util.Util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableScheduling
public class DarknetRequester extends NamedPipesRequester {

	public DarknetRequester() {
		super("darknet_anotation");
		// TODO Auto-generated constructor stub
	}

}
