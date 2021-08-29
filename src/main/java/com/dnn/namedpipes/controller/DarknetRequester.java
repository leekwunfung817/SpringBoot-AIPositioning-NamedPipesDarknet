package com.dnn.namedpipes.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.dnn.bean.DarknetResult;
import com.dnn.bean.DarknetResultSet;
import com.dnn.cmd.CloudIPCameraCMD;
import com.dnn.cmd.DarknetCMD;
import com.dnn.namedpipes.NamedPipesRequester;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Configuration
public class DarknetRequester {

	private HashMap<String, NamedPipesRequester> listDarknetRequester = new HashMap<String, NamedPipesRequester>();
	private HashMap<String, DarknetCMD> listDarknetCMD = new HashMap<String, DarknetCMD>();

	public void addDarkNet(String annotation) throws IOException {
		listDarknetCMD.put(annotation, new DarknetCMD(annotation));
		listDarknetRequester.put(annotation, new NamedPipesRequester(annotation));
	}

	public DarknetResultSet predict(String annotation, byte[] bytes) {
		return getListDarknetRequester().get(annotation).predict(bytes);
	}

	@PreDestroy
	public void preDistroy() {
		for (Map.Entry<String, DarknetCMD> entry : listDarknetCMD.entrySet()) {
			log.info("DarknetRequesterController preDistroy:" + entry.getKey() + "/" + entry.getValue());
			Process process = entry.getValue().getProcess();
			while (process.isAlive()) {
				process.destroy();
			}
		}
	}
}
