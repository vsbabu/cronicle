package org.vsbabu.cronicle.service;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;
import org.vsbabu.cronicle.domain.Run;
import org.vsbabu.cronicle.domain.RunStatus;

import java.util.List;

//TODO: remove repositoryRestService and put our own custom service
@RepositoryRestResource(collectionResourceRel = "runs", path = "runs")
public interface RunRepository extends Repository<Run, Long> {

	List<Run> findByCronIdOrderByScheduleTimeDesc(String cronid);
    List<Run> findByCronIdAndStatusOrderByScheduleTimeDesc(String cronid, RunStatus status);
    List<Run> findByStatusOrderByScheduleTimeDesc(RunStatus status);

	Run save(Run t);
	
	@Modifying
	@Transactional
	@Query(value="DELETE FROM cronicle_run WHERE cron_id = ?1 AND status=1 and schedule_time > now()", nativeQuery=true)
	void deleteFutureScheduled(String cronid);

	@Query(value="SELECT * FROM cronicle_run WHERE cron_id = ?1 AND status in (1,2) and schedule_time < now()", nativeQuery=true)
	List<Run> findByPastPendingRuns(String cronid);

	@Query(value="SELECT * FROM cronicle_run WHERE cron_id = ?1 AND status = 2 order by start_time desc", nativeQuery=true)
	List<Run> getCurrentRun(String cronid);
	
	Run  findById(Long id);

}
