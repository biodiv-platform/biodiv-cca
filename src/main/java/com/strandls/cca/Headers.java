/**
 * 
 */
package com.strandls.cca;

import javax.ws.rs.core.HttpHeaders;

import com.strandls.activity.controller.ActivitySerivceApi;
import com.strandls.user.controller.UserServiceApi;
import com.strandls.userGroup.controller.UserGroupSerivceApi;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class Headers {

	public ActivitySerivceApi addActivityHeader(ActivitySerivceApi activityService, String authHeaders) {
		activityService.getApiClient().addDefaultHeader(HttpHeaders.AUTHORIZATION, authHeaders);
		return activityService;
	}

	public UserServiceApi addUserHeaders(UserServiceApi userService, String authHeader) {
		userService.getApiClient().addDefaultHeader(HttpHeaders.AUTHORIZATION, authHeader);
		return userService;
	}

	public UserGroupSerivceApi addUserGroupHeader(UserGroupSerivceApi ugService, String authHeader) {
		ugService.getApiClient().addDefaultHeader(HttpHeaders.AUTHORIZATION, authHeader);
		return ugService;
	}
}
