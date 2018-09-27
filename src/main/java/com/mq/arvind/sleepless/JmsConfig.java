package com.mq.arvind.sleepless;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.ibm.mq.MQException;
import com.ibm.mq.constants.MQConstants;
import com.ibm.msg.client.wmq.compat.base.internal.MQQueue;
import com.ibm.msg.client.wmq.compat.base.internal.MQQueueManager;


@Configuration
public class JmsConfig {
	
	public static final int OPEN_OPTIONS = 
			MQConstants.MQOO_OUTPUT | 
			MQConstants.MQOO_INPUT_AS_Q_DEF |
			MQConstants.MQOO_INQUIRE;
	
	@Autowired
	private Environment environment;
	
	@Bean
	public MQQueueManager sourceQueueManager() throws MQException {
		Map<String, Object> props = new HashMap<>();
		props.put(MQConstants.CHANNEL_PROPERTY, environment.getProperty("mq.source.channel"));
		props.put(MQConstants.PORT_PROPERTY, Integer.parseInt(environment.getProperty("mq.source.port")));
		props.put(MQConstants.HOST_NAME_PROPERTY, environment.getProperty("mq.source.host"));
		return new MQQueueManager(environment.getProperty("mq.source.qm"), props);
	}
	
	@Bean
	public MQQueueManager destinationQueueManager() throws MQException {
		Map<String, Object> props = new HashMap<>();
		props.put(MQConstants.CHANNEL_PROPERTY, environment.getProperty("mq.dest.channel"));
		props.put(MQConstants.PORT_PROPERTY, Integer.parseInt(environment.getProperty("mq.dest.port")));
		props.put(MQConstants.HOST_NAME_PROPERTY, environment.getProperty("mq.dest.host"));
		return new MQQueueManager(environment.getProperty("mq.dest.qm"), props);
	}
	
	@Bean
	public MQQueue sourceQueue(MQQueueManager sourceQueueManager) throws MQException {
		return sourceQueueManager.accessQueue(environment.getProperty("mq.source.queue"), OPEN_OPTIONS);
	}
	
	@Bean
	public MQQueue destinationQueue(MQQueueManager destinationQueueManager) throws MQException {
		return destinationQueueManager.accessQueue(environment.getProperty("mq.dest.queue"), OPEN_OPTIONS);
	}
}
