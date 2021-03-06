package com.skl.cloud.foundation.batchjob;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.skl.cloud.model.job.JobDefinitionBean;
import com.skl.cloud.model.job.JobExecutionBean;
import com.skl.cloud.model.job.JobInstanceBean;
import com.skl.cloud.service.job.JobManagerService;

/**
 * <p> Simple job executor. <p>
 * 
 * <p>Creation Date and by: 2016/03/12 Author: liyangbin
 * 
 * @author $Author: liyangbin $
 * @version $Revision: 7143 $ $Date: 2016-03-16 17:01:30 +0800 (Wed, 16 Mar 2016) $
 */
public class SimpleJobExecutor implements JobExecutor {

	private static final Logger LOGGER = Logger.getLogger(SimpleJobExecutor.class);
	
	/**
	 * Job manager service.
	 */
	@Autowired
	private JobManagerService jobManagerService;

	/**
	 * Execute a job with the job context.
	 */
	@Override
	public final void execute(Job<?> job, JobContext jobContext) {
		if (!(job instanceof SimpleJob)) {
			throw new IllegalArgumentException("This executor only can execute the SimpleJob.");
		}
		SimpleJob<?> simpleJob = (SimpleJob<?>) job;

		// Load the definition for this job.
		loadJobDefinition(simpleJob);

		// Check the job is defined in db.
		if (simpleJob.getJobDefinition() == null) {
			throw new IllegalArgumentException("This job should be defined first in table \"batch_job\".");
		}

		if (!simpleJob.getJobDefinition().isStartFlag()) {
			LOGGER.info("The job[" + simpleJob.getJobId() + "]'s status is stopped in table \"batch_job\", complete processing.");
			return;
		}

		JobExecutionBean jobExecution = createJobExecuteInformation(simpleJob);

		preProcess(simpleJob, jobExecution);
		
		// process business logic
		start(simpleJob, jobContext);
		
		postProcess(simpleJob, jobExecution);
	}

	/**
	 * Start the job.
	 * 
	 * @param simpleJob
	 * @param jobContext
	 */
	private <T> void start(SimpleJob<T> simpleJob, JobContext jobContext) {
		boolean rollPoling = jobContext.isRollPoling();
		while (running(simpleJob)) {
			List<T> items = simpleJob.dataSource(jobContext);
			if (items == null || items.isEmpty()) {
				LOGGER.info("The job[" + simpleJob.getJobId() + "]'s datasource is empty, complete processing.");
				return;
			}
			LOGGER.info("The job[" + simpleJob.getJobId() + "] begin to batch processing " + items.size() + " items.");
			int failedNumber = 0;
			for (T item : items) {
				if (!running(simpleJob)) {
					LOGGER.info("The job[" + simpleJob.getJobId() + "]'s duration time is reached, complete processing.");
					return;
				}
				try {
					simpleJob.process(item, jobContext);
				} catch (Exception e) {
					failedNumber++;
					boolean continueExecute = simpleJob.processAfterException(item, jobContext, e);
					if (continueExecute) {
						LOGGER.warn("Job ignored above exception and continue process.");
					} else {
						LOGGER.warn("The job[" + simpleJob.getJobId() + "] have an exception, exit processing.");
						return;
					}
				}
			}
			LOGGER.info("The job[" + simpleJob.getJobId() + "] finished the batch processing " + items.size() + " items.");
			if (failedNumber == items.size()) {
				LOGGER.info("All " + items.size() + " items in one batch process failed by the job[" + simpleJob.getJobId() + "], force exit processing.");
				return;
			}
			if(!rollPoling) {
				LOGGER.warn("The job[" + simpleJob.getJobId() + "] have run once, exit processing.");
				return;
			}
		}
		LOGGER.info("The job[" + simpleJob.getJobId() + "]'s duration time is reached, complete processing.");
	}
	
	/**
	 * Check whether the job is running.
	 * 
	 * @param simpleJob
	 * @return running or not
	 */
	private final boolean running(SimpleJob<?> simpleJob) {
		JobDefinitionBean jobDefinition = simpleJob.getJobDefinition();
		return ((System.currentTimeMillis() - jobDefinition.getStartTime().getTime()) / 1000) < jobDefinition.getDurationSeconds();
	}

	/**
	 * Load the job definition for the job 
	 */
	private void loadJobDefinition(SimpleJob<?> job) {
		JobDefinitionBean jobDefinition = this.jobManagerService.getJobDefinition(job.getJobId());
		job.setJobDefinition(jobDefinition);
	}

	/**
	 * Create job execution information for the job.
	 * 
	 * @param job simple job
	 * @return job execution
	 */
	private JobExecutionBean createJobExecuteInformation(SimpleJob<?> job) {
		// reset the job execute time
		JobDefinitionBean jobDefinition = job.getJobDefinition();

		// create job instance
		JobInstanceBean jobInstance = new JobInstanceBean();
		jobInstance.setJobId(job.getJobId());
		jobInstance.setJobName(jobDefinition.getJobName());
		jobInstance.setJobKey(String.valueOf(System.currentTimeMillis()));
		this.jobManagerService.createJobInstance(jobInstance);

		// create job execution
		JobExecutionBean jobExecution = new JobExecutionBean();
		jobExecution.setJobInstanceId(jobInstance.getJobInstanceId());
		jobExecution.setStartTime(new Date());
		try {
			jobExecution.setServerName(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			LOGGER.warn("Get the host name error", e);
		}
		this.jobManagerService.createJobExecution(jobExecution);
		return jobExecution;
	}

	/**
	 * Reset the job definition before the job begin to process.
	 * 
	 * @param job simple job
	 * @param jobExecution job execution
	 */
	private void preProcess(SimpleJob<?> job, JobExecutionBean jobExecution) {
		// reset the job execute time
		JobDefinitionBean jobDefinition = job.getJobDefinition();
		jobDefinition.setStartTime(new Date());
		jobDefinition.setEndTime(null);
		this.jobManagerService.updateJobDefinition(jobDefinition);
	}

	/**
	 * Update the job definition before the job begin to process.
	 * 
	 * @param job simple job
	 * @param jobExecution job execution
	 */
	private void postProcess(SimpleJob<?> job, JobExecutionBean jobExecution) {
		// reset the job execute time
		JobDefinitionBean jobDefinition = job.getJobDefinition();
		jobDefinition.setEndTime(new Date());
		this.jobManagerService.updateJobDefinition(jobDefinition);

		// update job execution status
		jobExecution.setEndTime(new Date());
		final String status = "COMPLETED";
		jobExecution.setStatus(status);
		jobExecution.setExitCode(status);
		this.jobManagerService.updateJobExecution(jobExecution);
	}
}
