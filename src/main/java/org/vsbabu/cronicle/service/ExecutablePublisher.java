package org.vsbabu.cronicle.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.vsbabu.cronicle.domain.Cron;
import org.vsbabu.cronicle.domain.Run;

import reactor.bus.Event;
import reactor.fn.Consumer;

/**
 * Simple publisher that calls a specified executable in background with event
 * info
 *
 */
@Service("executablePublisher")
public class ExecutablePublisher implements Consumer<Event<Run>> {

	private static final Logger logger = LoggerFactory.getLogger(ExecutablePublisher.class);
	
	@Value("${run.event.executable}")private String runEventExecutable;

	@Autowired CronManagerService cronManager;
	
	public void accept(Event<Run> ev) {

		Run d = ev.getData();
		Cron c = cronManager.getCron(d.getCronId());
		
		logger.debug("Got event RUN." + d.getStatus() + " > " + d.getCronId());
		if (runEventExecutable != null && !runEventExecutable.isEmpty()) {
			StringBuffer cmd = new StringBuffer(runEventExecutable);
			cmd.append(" ");
			cmd.append(d.getStatus());
			cmd.append(" ");
			cmd.append(d.getCronId());
			cmd.append(" ");
			cmd.append((c!=null)?c.getName():"ORPHAN_RUN");
			logger.debug("Call cmd: " + cmd.toString());
			try {
				Runtime.getRuntime().exec(cmd.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
