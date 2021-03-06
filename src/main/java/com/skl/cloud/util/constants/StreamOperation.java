/**
 * $Header: http://192.168.139.235/svn/sklcloud/branches/Businesslogic/development/201605/sklcloud_feature_cwb_20160526/src/main/java/com/skl/cloud/util/constants/StreamOperation.java 7958 2016-04-22 05:58:28Z liyangbin $
 * $Revision: 7958 $
 * $Date: 2016-04-22 13:58:28 +0800 (Fri, 22 Apr 2016) $
 * ====================================================================
 *
 * Copyright (c) 2016 Sky Light Electronic (ShenZhen) Limited All 
 * Rights Reserved. This software is the confidential and proprietary
 * information of Sky Light Electronic (ShenZhen) Limited.You shall not
 * disclose such Confidential Information.
 *
 * ====================================================================
 */
package com.skl.cloud.util.constants;

import static com.skl.cloud.util.constants.StreamProcessStatus.Free;
import static com.skl.cloud.util.constants.StreamProcessStatus.InP2PPlaying;
import static com.skl.cloud.util.constants.StreamProcessStatus.LoadRelayError;
import static com.skl.cloud.util.constants.StreamProcessStatus.LoadingRelay;
import static com.skl.cloud.util.constants.StreamProcessStatus.ReadyForRelayUp;
import static com.skl.cloud.util.constants.StreamProcessStatus.ReadyRelay;
import static com.skl.cloud.util.constants.StreamProcessStatus.RelayErroFromSS;
import static com.skl.cloud.util.constants.StreamProcessStatus.ReleasingRelay;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skl.cloud.common.exception.BusinessException;
import com.skl.cloud.model.ipc.StreamStatusCount;

/**
 * @ClassName: StreamOperation
 * @Description: TODO
 * <p>Creation Date: 2016年4月19日 and by Author: yangbin </p>
 *
 * @author $Author: liyangbin $
 * @date $Date: 2016-04-22 13:58:28 +0800 (Fri, 22 Apr 2016) $
 * @version  $Revision: 7958 $
 */
/**
 * set stream status
 */
public enum StreamOperation{

	SCHEDULE_BEGIN(new StreamProcessStatus[] { Free }, LoadingRelay),
	
	SCHEDULE_ERROR(new StreamProcessStatus[] { LoadingRelay }, LoadRelayError),
	
	SCHEDULE_WAIT(new StreamProcessStatus[] { LoadingRelay }, ReadyForRelayUp),
	
	SCHEDULE_OK(new StreamProcessStatus[] { LoadingRelay }, ReadyRelay),
	
	P2P_START(new StreamProcessStatus[] { Free }, InP2PPlaying),
	
	P2P_END(new StreamProcessStatus[] { InP2PPlaying }, Free),
	
	RELEASE_START(new StreamProcessStatus[] { LoadingRelay, LoadRelayError, ReadyForRelayUp, ReadyRelay, RelayErroFromSS },	ReleasingRelay),
	
	RELEASE_END(new StreamProcessStatus[] { ReleasingRelay }, Free),
	
	RESET(new StreamProcessStatus[] { LoadingRelay, LoadRelayError, ReadyForRelayUp, ReadyRelay, RelayErroFromSS, ReleasingRelay }, Free);

	private final List<String> allowProcessStatusList;

	private final String processStatus;
	
	private static Logger logger = LoggerFactory.getLogger(StreamOperation.class);

	private StreamOperation(StreamProcessStatus[] allowProcessStatuses, StreamProcessStatus processStatus) {
		this.allowProcessStatusList = new ArrayList<String>();
		if (allowProcessStatuses != null) {
			for (StreamProcessStatus status : allowProcessStatuses) {
				this.allowProcessStatusList.add(status.toString());
			}
		}
		this.processStatus = processStatus != null ? processStatus.toString() : null;
	}

	public void setStreamStatus(StreamStatusCount stream) {
		String newProcessStatus = null;
		if (isAllowed(stream)) {
			newProcessStatus = processStatus;
		}
		if (newProcessStatus == null) {
			logger.warn("The IPC stream [sn:" + stream.getSn()
					+ " ,streamStauts:" + stream.getStreamStatus() + "] can't be [" + this.name() + "].");
			throw new BusinessException("");
		}
		StreamProcessStatus processStatus = StreamProcessStatus.valueOf(newProcessStatus);

		stream.setStreamStatus(processStatus.toString());
	}

	/**
	 * Is allow
	 * 
	 * @param stream
	 * @return
	 */
	public boolean isAllowed(StreamStatusCount stream) {
		return allowProcessStatusList.contains(stream.getStreamStatus());
	}

}
