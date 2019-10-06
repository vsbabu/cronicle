package org.vsbabu.cronicle.background;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.vsbabu.cronicle.domain.Cron;
import org.vsbabu.cronicle.service.CronManagerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JobCleaner {

	private static final Logger logger = LoggerFactory.getLogger(JobCleaner.class);
	
	@Autowired private CronManagerService cronManager;

	/**
	 * Go over all cronjobs; if nextRun is > maxCurRun, create a new schedule
	 * for nextRun Mark all other scheduled as DIDNTRUN
	 *
	 * This uses brute force scanning; we should rather keep nextRuns in an in
	 * memory cache and lookup only at that in slots of multiple cron scans. One
	 * scans every hour to DB and adds to memory. Memory scan runs every 5
	 * seconds etc.
	 *
	 * for now, run every 5 minutes - 300000; TODO: add  to property file
	 */
	@Scheduled(fixedRateString = "${jobcleaner.frequency}")
	@Transactional
	public void scheduleAndCleanup() {

		List<Cron> crons = cronManager.getAllCrons();
		logger.debug("Job cleaner cron started");
		for (Cron cron : crons) {
			cronManager.fixPastPendingRuns(cron);
			if (null == cronManager.getNextScheduleRun(cron))
				if (null == cronManager.getCurrentRun(cron)) {
					cronManager.scheduleNextRun(cron);
					logger.debug(cron.getName() + " :=> scheduled next run");
				}
		}

	}
}
