package org.vsbabu.cronicle.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name="cronicle_run")
public class Run implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;


	@Column(nullable = false)
	private String cronId; //making it as non-relational to make it easier to process

	@Column(nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date scheduleTime;

	@Column(nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
	private Date startTime;

	@Column(nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date endTime;

	@Column(nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private RunStatus status;
	
	public Run() {
        this.scheduleTime = new Date();
		this.status = RunStatus.SCHEDULED;
	}

	public Run(Cron cronjob, RunStatus status) {
		super();
		this.cronId = cronjob.getId();
		this.status = status;
	}

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public RunStatus getStatus() {
		return status;
	}

	public void setStatus(RunStatus status) {
		this.status = status;
	}

    public Date getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(Date scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public String getCronId() {
		return cronId;
	}

	public void setCronId(String cronId) {
		this.cronId = cronId;
	}

}
