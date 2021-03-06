/**
 * $Header: http://192.168.139.235/svn/sklcloud/branches/Businesslogic/development/201605/sklcloud_feature_cwb_20160526/src/main/java/com/skl/cloud/util/constants/StreamProcessStatus.java 8911 2016-05-20 02:40:24Z liyangbin $
 * $Revision: 8911 $
 * $Date: 2016-05-20 10:40:24 +0800 (Fri, 20 May 2016) $
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

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: StreamProcessStatus
 * @Description: stream process status
 * <p>Creation Date: 2016年4月19日 and by Author: yangbin </p>
 *
 * @author $Author: liyangbin $
 * @date $Date: 2016-05-20 10:40:24 +0800 (Fri, 20 May 2016) $
 * @version  $Revision: 8911 $
 */
public enum StreamProcessStatus {
	Free(false, "IPC Stream is free"),
	InP2PPlaying(true, "IPC Stream is in p2p for live"),
	LoadingRelay(true, "Stream is in relay live Schedule"),
	LoadRelayError(true, "Stream Schedule exception"),
	ReadyForRelayUp(true, "Stream Schedule finish, checking stream if up to wowza success"),
	ReadyRelay(true, "Stream Schedule finish, stream have up to wowza"),
	RelayErroFromSS(false, "wowza report exception by stream error or no client pull stream"),
	ReleasingRelay(false, "stream in releasing resource in sub system and IPC side");
	
	private static final List<StreamProcessStatus> inScheduleProcessStatuses = new ArrayList<StreamProcessStatus>();

	static {
		for (StreamProcessStatus processStatus : StreamProcessStatus.values()) {
			if (processStatus.inStreamSchedule) {
				inScheduleProcessStatuses.add(processStatus);
			}
		}
	}
	
	//status is in stream schedule
	private final boolean inStreamSchedule;
	//status remark
	private final String remark;
	
	private StreamProcessStatus(boolean inStreamSchedule, String remark) {
		this.inStreamSchedule = inStreamSchedule;
		this.remark = remark;
	}
	
	/**
	 * Get the all Stream process status.
	 * 
	 * @return list of StreamProcessStatus
	 */
	public static List<StreamProcessStatus> getStreamInScheduleProcessStatuses() {
		return new ArrayList<StreamProcessStatus>(inScheduleProcessStatuses);
	}
	
	public static boolean isExistsStreamStatus(String status){
        boolean ret = false;
        StreamProcessStatus[] types = StreamProcessStatus.values();
        for(StreamProcessStatus type: types){
            if(type.name().equals(status)){
                return true;
            }
        }
        return ret;
    }
	
	/**
	 * getter method
	 * @return the inStreamSchedule
	 */
	public boolean isInStreamSchedule() {
		return inStreamSchedule;
	}

	/**
	 * getter method
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}
}

