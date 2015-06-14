package org.vsbabu.cronicle.domain;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.scheduling.support.CronSequenceGenerator;


@Entity
@Table(name="cronicle_cron")
public class Cron implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;


  @Column(nullable=false) private String name;
  @Column(nullable=false) private String description;
  @Column(nullable=false) private String expression;
  @Column(nullable=true)  private String timezone;
  @Column(nullable=false) private int gracePeriodForStart = 0;
  @Column(nullable=true) private int maxRuntime = -1;


  @Column(nullable=true) private Long lastRunId;

  public Cron() {
  }

  public Cron(String name, String description, String cronExpression, String timezone, int gracePeriodForStart, int maxRunTime) {
    this.name = name;
    this.description = description;
    this.expression = cronExpression; //TODO: add validation
    setTimezone(timezone);
    this.gracePeriodForStart = gracePeriodForStart;
    this.maxRuntime = maxRunTime;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }



  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  @Transient
  public String getKey() {
    return this.id;
  }

  @SuppressWarnings("static-access")
  @Transient
  public Date getNextRun() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(this.getNextRunWithoutGrace());
    cal.add(Calendar.MINUTE, this.gracePeriodForStart);
    return cal.getTime();
  }

  @SuppressWarnings("static-access")
  @Transient
  public Date getNextRunWithoutGrace() {
    CronSequenceGenerator csg;
    if (this.getTimezone() == null) {
      csg = new CronSequenceGenerator(this.expression);
    } else {
      TimeZone tz = TimeZone.getDefault();
      try {
        csg = new CronSequenceGenerator(this.expression, tz.getTimeZone(this.timezone));
      } catch (Exception ex) {
        csg = new CronSequenceGenerator(this.expression);
      }
    }
    Calendar cal = Calendar.getInstance();
    cal.setTime(csg.next(new Date()));
    return cal.getTime();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getMaxRuntime() {
    return maxRuntime;
  }

  public void setMaxRuntime(int maxRuntime) {
    this.maxRuntime = maxRuntime;
  }

  public int getGracePeriodForStart() {
    return gracePeriodForStart;
  }

  public void setGracePeriodForStart(int gracePeriodForStart) {
    this.gracePeriodForStart = gracePeriodForStart;
  }

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    if (timezone!=null && timezone.trim().isEmpty())
      this.timezone = null;
    else
      this.timezone = timezone;
  }

  public Long getLastRunId() {
    return lastRunId;
  }

  public void setLastRunId(Long lastRunId) {
    this.lastRunId = lastRunId;
  }


}

	
