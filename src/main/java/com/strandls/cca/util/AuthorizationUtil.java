package com.strandls.cca.util;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.pac4j.core.profile.CommonProfile;

import com.strandls.authentication_utility.util.AuthUtil;

import net.minidev.json.JSONArray;

public class AuthorizationUtil {

	public static final String UNAUTHORIZED_MESSAGE = "User is unauthorized to do the action";

	private AuthorizationUtil() {
	}

	public static boolean checkAuthorization(HttpServletRequest request, List<Permissions> list, String objectId) {
		if (request == null)
			return false;

		CommonProfile profile = AuthUtil.getProfileFromRequest(request);
		if (profile == null)
			return false;

		JSONArray roles = (JSONArray) profile.getAttribute("roles");
		if (roles.contains(Permissions.ROLE_ADMIN.value))
			return true;

		List<Object> permittedRoles = roles.stream().filter(list::contains).collect(Collectors.toList());

		if (!permittedRoles.isEmpty())
			return true;

		if (objectId != null)
			return objectId.equals(profile.getId());

		return false;
	}
}
