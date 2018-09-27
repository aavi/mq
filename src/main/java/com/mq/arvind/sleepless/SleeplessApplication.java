package com.mq.arvind.sleepless;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.ibm.mq.constants.MQConstants;
import com.ibm.msg.client.wmq.compat.base.internal.MQMessage;
import com.ibm.msg.client.wmq.compat.base.internal.MQPutMessageOptions;
import com.ibm.msg.client.wmq.compat.base.internal.MQQueue;

@SpringBootApplication
public class SleeplessApplication {

	public static final MQPutMessageOptions pmo = new MQPutMessageOptions();
	static {
		pmo.options = MQConstants.MQPMO_ASYNC_RESPONSE;
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(SleeplessApplication.class, args);
		final MQQueue sourceQueue = (MQQueue) context.getBean("sourceQueue");
		final MQQueue destinationQueue = (MQQueue) context.getBean("destinationQueue");
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(() -> {
			try {
				int msgs = 0;
				while(sourceQueue.isOpen() && destinationQueue.isOpen() && sourceQueue.getCurrentDepth() > 0) {
					MQMessage message = new MQMessage();
					sourceQueue.get(message);
					destinationQueue.put(message, pmo);
					msgs++;
				}
				if(msgs > 0) {
					System.out.println(String.format("Moved %d messages on %s", msgs, new Date()));
				}
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}, 1, 300, TimeUnit.SECONDS);
		//((ScheduledThreadPoolExecutor)executor).setContinueExistingPeriodicTasksAfterShutdownPolicy(true);
	}
}

