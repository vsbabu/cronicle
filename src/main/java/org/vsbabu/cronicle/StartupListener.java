package org.vsbabu.cronicle;

import static reactor.bus.selector.Selectors.$;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.vsbabu.cronicle.domain.RunStatus;
import org.vsbabu.cronicle.service.ExecutablePublisher;

import reactor.bus.EventBus;

@Component
public class StartupListener implements
		ApplicationListener<ContextRefreshedEvent> {

	private static final Logger logger = LoggerFactory.getLogger(StartupListener.class);
	


	@Autowired
	private EventBus eventBus;

	@Autowired
	private ExecutablePublisher publisher;

	public void onApplicationEvent(ContextRefreshedEvent event) {
		for (RunStatus status : RunStatus.values()) {
			logger.debug("Registering listener for RUN." + status.toString());
			eventBus.on($("RUN." + status.toString()), publisher);
		}
	}
}
