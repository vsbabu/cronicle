package org.vsbabu.cronicle.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.vsbabu.cronicle.domain.Cron;
import org.vsbabu.cronicle.domain.Run;
import org.vsbabu.cronicle.service.CronManagerService;
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

	@Autowired
	private RunRepository runRepository;
	@Autowired
	private CronManagerService cronManager;

	// TODO: add a today's list of runs; or between last 12 and next 12 hours
	@RequestMapping(value = "/{job}", method = RequestMethod.GET)
	public List<Run> jobRuns(@PathVariable("job") String jobid) {
		Cron job = cronManager.getCron(jobid);
		if (job == null)
			return null;
		return runRepository.findByCronIdOrderByScheduleTimeDesc(jobid);
	}

	@RequestMapping(value = "/{job}/start", method = RequestMethod.GET)
	public String jobStart(@PathVariable("job") String jobid) {
		Cron job = cronManager.getCron(jobid);
		if (job == null)
			return "Not found";
		cronManager.startRun(job);
		return "Started";
	}
	@RequestMapping(value = "/start", method = RequestMethod.GET)
	public String jobStartH(@RequestHeader("jobId") String jobId) {
		return jobStart(jobId);
	}

	@RequestMapping(value = "/{job}/pass", method = RequestMethod.GET)
	public String jobSuccess(@PathVariable("job") String jobid) {
		Cron job = cronManager.getCron(jobid);
		if (job == null)
			return "Not found";
		cronManager.passRun(job);
		return "Success";
	}
	@RequestMapping(value = "/pass", method = RequestMethod.GET)
	public String jobSuccessH(@RequestHeader("jobId") String jobId) {
		return jobSuccess(jobId);
	}

	@RequestMapping(value = "/{job}/fail", method = RequestMethod.GET)
	public String jobFailed(@PathVariable("job") String jobid) {
		Cron job = cronManager.getCron(jobid);
		if (job == null)
			return "Not found";
		cronManager.failRun(job);
		return "Failed";
	}
	@RequestMapping(value = "/fail", method = RequestMethod.GET)
	public String jobFailedH(@RequestHeader("jobId") String jobId) {
		return jobFailed(jobId);
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
