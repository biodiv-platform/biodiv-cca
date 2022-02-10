package com.strandls.cca.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.pac4j.core.profile.CommonProfile;

import com.strandls.authentication_utility.util.AuthUtil;
import com.strandls.cca.pojo.CCAData;

import net.minidev.json.JSONArray;

public class AuthorizationUtil {

	public static final String UNAUTHORIZED_MESSAGE = "User is unauthorized to do the action";

	private AuthorizationUtil() {
	}

	public static List<String> getRoles(HttpServletRequest request) {
		List<String> permissions = new ArrayList<>();
		if (request == null)
			return permissions;

		CommonProfile profile = AuthUtil.getProfileFromRequest(request);
		if (profile == null)
			return permissions;

		JSONArray roles = (JSONArray) profile.getAttribute("roles");
		for (int i = 0; i < roles.size(); i++) {
			permissions.add((String) roles.get(i));
		}
		return permissions;
	}

	public static boolean checkAuthorization(HttpServletRequest request, List<Permissions> list, String userId) {
		if (request == null)
			return false;

		CommonProfile profile = AuthUtil.getProfileFromRequest(request);
		if (profile == null)
			return false;

		JSONArray roles = (JSONArray) profile.getAttribute("roles");
		if (roles.contains(Permissions.ROLE_ADMIN.value))
			return true;

		Set<String> stringList = list.stream().map(Permissions::name).collect(Collectors.toSet());

		List<Object> permittedRoles = roles.stream().filter(stringList::contains).collect(Collectors.toList());

		if (!permittedRoles.isEmpty())
			return true;

		if (userId != null)
			return userId.equals(profile.getId());

		return false;
	}

	public static boolean checkAuthorization(HttpServletRequest request, List<Permissions> list, String userId, CCAData ccaData) {

		return checkAuthorization(request, list, userId) || ccaData.getAllowedUsers().contains(userId);
	}
}
