package com.skl.cloud.service.job.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skl.cloud.dao.job.JobManagerMapper;
import com.skl.cloud.model.job.JobDefinitionBean;
import com.skl.cloud.model.job.JobExecutionBean;
import com.skl.cloud.model.job.JobInstanceBean;
import com.skl.cloud.service.job.JobManagerService;

/**
 * <p>Base implementation of {@link JobManagerService}.
 * 
 * <p>Creation Date and by: 2016/03/12 Author: liyangbin </p>
 * 
 * @author $Author: liyangbin $
 * @version $Revision: 7760 $ $Date: 2016-04-14 17:32:36 +0800 (Thu, 14 Apr 2016) $
 */
@Service
public class JobManagerServiceImpl implements JobManagerService {
	
	@Autowired
	private JobManagerMapper jobManagerDao;
	
	/**
	 * Get a job definition.
	 * 
	 * @param jobId the {@link JobDefinitionBean} object
	 */
	@Transactional(readOnly = true)
	public JobDefinitionBean getJobDefinition(long jobId) {
		return this.jobManagerDao.queryJobDefinition(jobId);
	}
	
	/**
	 * Update the job definition.
	 * 
	 * @param jobId the {@link JobDefinitionBean} object
	 */
	@Transactional
	public void updateJobDefinition(JobDefinitionBean jobDefinition) {
		this.jobManagerDao.updateJobDefinition(jobDefinition);
	}
	
	/**
	 * Create a new job instance.
	 * 
	 * @param jobInstance the {@link JobInstanceBean} object
	 */
	@Override
	@Transactional
	public void createJobInstance(JobInstanceBean jobInstance) {
		this.jobManagerDao.insertJobInstance(jobInstance);
	}

	/**
	 * Update the job instance.
	 * 
	 * @param jobInstance the {@link JobInstanceBean} object
	 */
	@Override
	@Transactional
	public void updateJobInstance(JobInstanceBean jobInstance) {
		this.jobManagerDao.updateJobInstance(jobInstance);
	}

	/**
	 * Create a new job execution.
	 * 
	 * @param jobExecution the {@link JobExecutionBean} object
	 */
	@Override
	@Transactional
	public void createJobExecution(JobExecutionBean jobExecution) {
		this.jobManagerDao.insertJobExecution(jobExecution);
	}

	/**
	 * Update the job execution.
	 * 
	 * @param jobExecution the {@link JobExecutionBean} object
	 */
	@Override
	@Transactional
	public void updateJobExecution(JobExecutionBean jobExecution) {
		this.jobManagerDao.updateJobExecution(jobExecution);
	}
	
	/**
	 * Delete the job instance.
	 * 
	 * @param jobInstanceId
	 */
	@Override
	@Transactional
	public void deleteJobInstance(long jobInstanceId) {
		this.jobManagerDao.deleteJobInstance(jobInstanceId);
		this.jobManagerDao.deleteJobExecution(jobInstanceId);
	}
	
	/**
	 * Get the historical job instance ids.
	 * 
	 * @param daysAgo some days ago
	 * @return job instance ids
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Long> getHistoricalJobInstanceIds(int daysAgo) {
		return this.jobManagerDao.queryHistoricalJobInstanceIds(daysAgo);
	}
	
}
