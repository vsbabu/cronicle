package org.vsbabu.cronicle.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vsbabu.cronicle.domain.Cron;
import org.vsbabu.cronicle.domain.Run;
import org.vsbabu.cronicle.domain.RunFlag;
import org.vsbabu.cronicle.domain.RunStatus;

import reactor.bus.Event;
import reactor.bus.EventBus;

@Service("CronManager")
public class CronManagerService {

	@Autowired
	private RunRepository runRepository;
	@Autowired
	private CronRepository cronRepository;

	@Autowired
	EventBus eventBus;

	private static final Logger logger = LoggerFactory
			.getLogger(CronManagerService.class);
	// if it is already marked as ran too long, at some point
	// we should mark it as failed.
	private static final int MULTIPLE_TO_WAIT_BEFORE_AUTOFAILED = 5;

	public List<Cron> getAllCrons() {
		return cronRepository.findAll();
	}

	public Cron getCron(String cronid) {
		return cronRepository.findById(cronid);
	}

	public Run getCurrentRun(Cron cron) {
		List<Run> runs = runRepository.getCurrentRun(cron.getId());
		if (runs == null || runs.isEmpty())
			return null;
		return runs.get(0);
	}

	public Run getNextScheduleRun(Cron cron) {
		List<Run> runs = runRepository
				.findByCronIdAndStatusOrderByScheduleTimeDesc(cron.getId(),
						RunStatus.SCHEDULED);
		Run run = null;
		for (Run it : runs) {
			run = it;
			break;
		}
		return run;
	}

	public Run scheduleNextRun(Cron cron) {
		// safe way - delete all future scheduled runs and schedule a new one
		runRepository.deleteFutureScheduled(cron.getId());
		Run r = new Run(cron, RunStatus.SCHEDULED);
		r.setScheduleTime(cron.getNextRun());
		runRepository.save(r);
		return r;
	}

	public void fixPastPendingRuns(Cron cron) {
		List<Run> runs = runRepository.findByPastPendingRuns(cron.getId());
		Date current = new Date();
		for (Run r : runs) {
			if (r.getStatus().isWip() && cron.getMaxRuntime() >= 0) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(r.getStartTime());
				cal.add(Calendar.MINUTE, cron.getMaxRuntime());
				if (current.after(cal.getTime())) {
					if (!RunFlag.RAN_TOO_LONG.equals(r.getFlag())) {
						r.setEndTime(current);
						setRunFlag(cron, r, RunFlag.RAN_TOO_LONG);
						logger.warn(cron.getName() + " :=> RAN_TOO_LONG");
					} else {
						cal.add(Calendar.MINUTE, cron.getMaxRuntime()
								* (MULTIPLE_TO_WAIT_BEFORE_AUTOFAILED - 1));
						if (current.after(cal.getTime())) {
							r.setEndTime(current);
							moveRunStatusTo(cron, r, RunStatus.FAILED);
							logger.warn(cron.getName() + " :=> FAILED");
						}
					}
				}
			}
			if (RunStatus.SCHEDULED.equals(r.getStatus())) {
				moveRunStatusTo(cron, r, RunStatus.DID_NOT_RUN);
				logger.info(cron.getName() + " :=> DID_NOT_RUN");
			}
		}
	}

	public void startRun(Cron cron) {
		Run run = getCurrentRun(cron);
		if (run != null)
			return; // no parallel runs tracked

		run = getNextScheduleRun(cron); // always pick a scheduled run even if
										// it is far out in future
		if (run == null) {
			run = makeRunWithoutSchedule(cron, RunStatus.UNSCHEDULED);
		}
		run.setStartTime(new Date());
		moveRunStatusTo(cron, run, RunStatus.RUNNING);
	}

	public void passRun(Cron cron) {
		Run run = getCurrentRun(cron);
		if (run == null)
			run = getNextScheduleRun(cron);
		if (run == null) {
			run = makeRunWithoutSchedule(cron, RunStatus.UNSCHEDULED);
		}
		run.setEndTime(new Date());
		moveRunStatusTo(cron, run, RunStatus.SUCCESS);
		scheduleNextRun(cron);
	}

	public void failRun(Cron cron) {
		Run run = getCurrentRun(cron);
		if (run == null)
			getNextScheduleRun(cron);
		if (run == null) {
			run = makeRunWithoutSchedule(cron, RunStatus.UNSCHEDULED);
		}
		run.setEndTime(new Date());
		moveRunStatusTo(cron, run, RunStatus.FAILED);
		scheduleNextRun(cron);
	}

	private Run makeRunWithoutSchedule(Cron cron, RunStatus status) {
		Run r = new Run(cron, status);
		runRepository.save(r);
		return r;
	}

	private void moveRunStatusTo(Cron cron, Run run, RunStatus status) {
		if (run == null || cron == null || status == null)
			return;
		run.setStatus(status);
		if (status.isWip())
			run.setStartTime(new Date());
		runRepository.save(run);
		cron.setLastRun(run);
		cronRepository.save(cron);
		String eventName = "RUN." + status.toString();
		logger.debug("Raising Event : " + eventName);
		eventBus.notify(eventName, Event.wrap(run));
	}

	private void setRunFlag(Cron cron, Run run, RunFlag flag) {
		if (run == null || cron == null || flag == null)
			return;
		run.setFlag(flag);
		String eventName = "RUN." + flag.toString();
		logger.debug("Raising Event : " + eventName);
		eventBus.notify(eventName, Event.wrap(run));
	}

	public List<Cron> findAllWithLastRunNew() {
		List<Cron> crons = cronRepository.findAll();
		for (Cron c : crons)
			c.getLastRun();
		return crons;
	}
}
