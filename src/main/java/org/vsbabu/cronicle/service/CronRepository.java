package org.vsbabu.cronicle.service;

import java.util.List;

import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.vsbabu.cronicle.domain.Cron;

//TODO: remove repositoryRestService and put our own custom service
@RepositoryRestResource(collectionResourceRel = "cron", path = "crons")
public interface CronRepository extends Repository<Cron, String> {

	//Cron findOne(String id);
	Cron findById(String id);

	List<Cron> findAll();

	Cron save(Cron t);

	// FIXME: DELETE isn't exposed
}
