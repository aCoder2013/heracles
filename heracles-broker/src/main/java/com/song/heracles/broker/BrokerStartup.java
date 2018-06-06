package com.song.heracles.broker;

import com.song.heracles.broker.config.BrokerConfiguration;
import com.song.heracles.common.util.ConfigUtils;
import com.song.heracles.broker.service.BrokerService;
import com.song.heracles.common.exception.HeraclesException;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;

/**
 * @author song
 */
public class BrokerStartup {

	public static void main(String[] args) throws HeraclesException, InterruptedException {
		Yaml yaml = new Yaml();
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		BrokerConfiguration brokerConfiguration = yaml.loadAs(classloader.getResourceAsStream("heracles.yaml"), BrokerConfiguration.class);
		ConfigUtils.validate(brokerConfiguration);
		BrokerService brokerService = new BrokerService(brokerConfiguration);
		brokerService.start();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				brokerService.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}));
		// FIXME: 2018/5/13 shutdown,cleanup dlog resources
		brokerService.waitUntilClosed();
	}

}
