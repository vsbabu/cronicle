package org.vsbabu.cronicle.service;

import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
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


}
