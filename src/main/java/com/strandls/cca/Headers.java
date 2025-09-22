/**
 *
 */
package com.strandls.cca;

import jakarta.ws.rs.core.HttpHeaders;

import com.strandls.activity.controller.ActivityServiceApi;
import com.strandls.user.controller.UserServiceApi;
import com.strandls.userGroup.controller.UserGroupServiceApi;

/**
 * @author Abhishek Rudra
 *
 *
 */
public class Headers {

	public ActivityServiceApi addActivityHeader(ActivityServiceApi activityService, String authHeaders) {
		activityService.getApiClient().addDefaultHeader(HttpHeaders.AUTHORIZATION, authHeaders);
		return activityService;
	}

	public UserServiceApi addUserHeaders(UserServiceApi userService, String authHeader) {
		userService.getApiClient().addDefaultHeader(HttpHeaders.AUTHORIZATION, authHeader);
		return userService;
	}

	public UserGroupServiceApi addUserGroupHeader(UserGroupServiceApi ugService, String authHeader) {
		ugService.getApiClient().addDefaultHeader(HttpHeaders.AUTHORIZATION, authHeader);
		return ugService;
	}
}
