/**
 * 
 */
package com.strandls.cca.service.impl;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.activity.controller.ActivitySerivceApi;
import com.strandls.activity.pojo.CCAActivityLogging;
import com.strandls.cca.Headers;

/**
 * 
 * @author vilay
 *
 */
public class LogActivities {

	@Inject
	private ActivitySerivceApi activityService;

	@Inject
	private Headers headers;

	private final Logger logger = LoggerFactory.getLogger(LogActivities.class);

	public void logCCAActivities(String authHeader, String activityDescription, Long rootObjectId,
			Long subRootObjectId, String rootObjectType, Long activityId, String activityType) {
		try {

			CCAActivityLogging activityLogging = new CCAActivityLogging();
			activityLogging.setActivityDescription(activityDescription);
			activityLogging.setActivityId(activityId);
			activityLogging.setActivityType(activityType);
			activityLogging.setRootObjectId(rootObjectId);
			activityLogging.setRootObjectType(rootObjectType);
			activityLogging.setSubRootObjectId(subRootObjectId);
			activityService = headers.addActivityHeader(activityService, authHeader);
			activityService.logCCAActivities(activityLogging);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

}
