CREATE TABLE cronicle_cron (
  id varchar(255) NOT NULL,
  name varchar(255) NOT NULL,
  expression varchar(255) NOT NULL,
  description varchar(255) NOT NULL,
  grace_period_for_start int(11) NOT NULL,
  last_run_status int(1) DEFAULT NULL,
  max_runtime int(11) DEFAULT NULL,
  timezone varchar(255) DEFAULT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE cronicle_run (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  cron_id varchar(255) NOT NULL,
  end_time datetime DEFAULT NULL,
  schedule_time datetime DEFAULT NULL,
  start_time datetime DEFAULT NULL,
  status int(1) NOT NULL,
  PRIMARY KEY (id)
);

CREATE INDEX idx_cronicle_runs on cronicle_run(cron_id, status, schedule_time);

SHOW TABLES;
