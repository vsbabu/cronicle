package org.vsbabu.cronicle.domain;

import java.util.Date;

@SuppressWarnings("serial")
public class CronWithLastRun extends Cron {

	
	public RunStatus getLastRunStatus() {
		return lastRunStatus;
	}
	public void setLastRunStatus(RunStatus lastRunStatus) {
		this.lastRunStatus = lastRunStatus;
	}
	public RunFlag getLastRunFlag() {
		return lastRunFlag;
	}
	public void setLastRunFlag(RunFlag lastRunFlag) {
		this.lastRunFlag = lastRunFlag;
	}
	public Date getScheduleTime() {
		return scheduleTime;
	}
	public void setScheduleTime(Date scheduleTime) {
		this.scheduleTime = scheduleTime;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	private RunStatus lastRunStatus;
	private RunFlag lastRunFlag;
	private Date scheduleTime;
	private Date startTime;
	private Date endTime;
	
}
