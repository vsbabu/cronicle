package org.vsbabu.cronicle.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vsbabu.cronicle.domain.CronWithLastRun;
import org.vsbabu.cronicle.service.CronManagerService;

@RestController

public class Crons {

	@Autowired CronManagerService cms;
	
	@RequestMapping("/crons/list") 
	public List<CronWithLastRun> getAllWithLastRun(){
		return cms.findAllWithLastRun();
	}
	
}
