package org.vsbabu.cronicle.background;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.vsbabu.cronicle.domain.Cron;
import org.vsbabu.cronicle.domain.Run;
import org.vsbabu.cronicle.domain.RunStatus;
import org.vsbabu.cronicle.service.CronRepository;
import org.vsbabu.cronicle.service.RunRepository;

@Component
public class JobCleaner {

	private static final int MAX_JOBS_TO_SCAN_PER_ID = 10;
	
	@Autowired private CronRepository cronRepository;
	@Autowired private RunRepository runRepository;

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
	@Scheduled(fixedRate = 300000)
	@Transactional
	public void schedule() {

		List<Cron> crons = cronRepository.findAll();

		for (Cron job : crons) {
			Date current = new Date();
			List<Run> runs = runRepository.findByCronIdOrderByScheduleTimeDesc(job.getId());
			boolean addnewschedule = true;
			int count = 0;
			for (Run it : runs) {
				count++;
				if (it.getStatus().equals(RunStatus.SCHEDULED)) {
					if (it.getScheduleTime().before(current)) {
						it.setStatus(RunStatus.DID_NOT_RUN);
						runRepository.save(it);
						job.setLastRunStatus(RunStatus.DID_NOT_RUN);
						cronRepository.save(job);
					} else {
						addnewschedule = false;
					}
				}
				if (it.getStatus().equals(RunStatus.RUNNING) && job.getMaxRuntime() >= 0) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(it.getStartTime());
					cal.add(Calendar.MINUTE, job.getMaxRuntime());
					if(current.after(cal.getTime())) {
						it.setStatus(RunStatus.RAN_TOO_LONG);
						it.setEndTime(current);
						runRepository.save(it);
						job.setLastRunStatus(RunStatus.RAN_TOO_LONG);
						cronRepository.save(job);
					}
				}
				if (count > MAX_JOBS_TO_SCAN_PER_ID)
					break;
			}
			if (addnewschedule) {
				Run r = new Run(job);
				r.setScheduleTime(job.getNextRun());
				r.setStatus(RunStatus.SCHEDULED);
				runRepository.save(r);
			}
		}

	}
}
