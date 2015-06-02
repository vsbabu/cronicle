package org.vsbabu.cronicle.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vsbabu.cronicle.domain.Cron;
import org.vsbabu.cronicle.domain.Run;
import org.vsbabu.cronicle.domain.RunStatus;
import org.vsbabu.cronicle.service.CronRepository;
import org.vsbabu.cronicle.service.RunRepository;

/**
 * Job controllers. Note that this doesn't guard against same job running
 * simultaneously from multiple machines. For that, one needs to send a uniqueid
 * back and let the runner call the updates with that id. Too complicated for
 * the consumer.
 */

@RestController
@RequestMapping(value = "/job")
public class Jobs {

	@Autowired private CronRepository cronRepository;
	@Autowired private RunRepository runRepository;

	// TODO: add a today's list of runs; or between last 12 and next 12 hours
	@RequestMapping(value = "/{job}", method = RequestMethod.GET)
	public List<Run> jobRuns(@PathVariable("job") String jobid) {
		Cron job = cronRepository.findById(jobid);
		if (job == null)
			return null;
		return runRepository.findByCronIdOrderByScheduleTimeDesc(jobid);
	}

	@RequestMapping(value = "/{job}/start", method = RequestMethod.GET)
	public String jobStart(@PathVariable("job") String jobid) {
		Cron job = cronRepository.findById(jobid);
		if (job == null)
			return "Not found";
		List<Run> runs = runRepository
				.findByCronIdAndStatusOrderByScheduleTimeDesc(jobid,
						RunStatus.SCHEDULED);
		Run run = null;
		for (Run it : runs) {
			run = it;
			break;
		}
		if (run == null)
			run = new Run(job);
		run.setStatus(RunStatus.RUNNING);
		run.setStartTime(new Date());
		runRepository.save(run);
		job.setLastRunStatus(RunStatus.RUNNING);
		cronRepository.save(job);
		return "Started";
	}

	@RequestMapping(value = "/{job}/pass", method = RequestMethod.GET)
	public String jobSuccess(@PathVariable("job") String jobid) {
		Cron job = cronRepository.findById(jobid);
		if (job == null)
			return "Not found";
		List<Run> runs = runRepository
				.findByCronIdOrderByScheduleTimeDesc(jobid);
		Run run = null;
		for (Run it : runs) {
			if (it.getStatus() == RunStatus.SCHEDULED
					|| it.getStatus() == RunStatus.RUNNING) {
				run = it;
				break;
			}
		}
		if (run == null)
			run = new Run(job);
		if (run.getStartTime() == null)
			run.setStartTime(new Date());
		run.setStatus(RunStatus.SUCCESS);
		run.setEndTime(new Date());
		runRepository.save(run);
		job.setLastRunStatus(RunStatus.SUCCESS);
		cronRepository.save(job);

		return "Success";
	}

	@RequestMapping(value = "/{job}/fail", method = RequestMethod.GET)
	public String jobFailed(@PathVariable("job") String jobid) {
		Cron job = cronRepository.findById(jobid);
		if (job == null)
			return "Not found";
		List<Run> runs = runRepository
				.findByCronIdOrderByScheduleTimeDesc(jobid);
		Run run = null;
		for (Run it : runs) {
			if (it.getStatus() == RunStatus.SCHEDULED
					|| it.getStatus() == RunStatus.RUNNING) {
				run = it;
				break;
			}
		}
		if (run == null)
			run = new Run(job);
		if (run.getStartTime() == null)
			run.setStartTime(new Date());
		run.setStatus(RunStatus.FAILED);
		run.setEndTime(new Date());
		runRepository.save(run);
		job.setLastRunStatus(RunStatus.FAILED);
		cronRepository.save(job);
		return "Failed";
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ErrorMessage handleException(MethodArgumentNotValidException ex) {
		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		List<ObjectError> globalErrors = ex.getBindingResult()
				.getGlobalErrors();
		List<String> errors = new ArrayList<>(fieldErrors.size()
				+ globalErrors.size());
		String error;
		for (FieldError fieldError : fieldErrors) {
			error = fieldError.getField() + ", "
					+ fieldError.getDefaultMessage();
			errors.add(error);
		}
		for (ObjectError objectError : globalErrors) {
			error = objectError.getObjectName() + ", "
					+ objectError.getDefaultMessage();
			errors.add(error);
		}
		return new ErrorMessage(errors);
	}

}
