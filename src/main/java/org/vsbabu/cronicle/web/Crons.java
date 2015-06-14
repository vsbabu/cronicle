package org.vsbabu.cronicle.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vsbabu.cronicle.domain.Cron;
import org.vsbabu.cronicle.service.CronManagerService;

/**
 * 
 * Public facing controller for crons.
 * 
 * At some point this will be used rather than RestJPARepository
 * 
 */
@RestController
public class Crons {

	@Autowired
	CronManagerService cms;

	@RequestMapping("/crons/list")
	public List<Cron> getAllWithLastRun() {
		return cms.findAllWithLastRunNew();
	}

}
