package com.strandls.cca.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.hibernate.ObjectNotFoundException;
import org.pac4j.core.profile.CommonProfile;

import com.strandls.activity.pojo.Activity;
import com.strandls.activity.pojo.CcaPermission;
import com.strandls.activity.pojo.CommentLoggingData;
import com.strandls.authentication_utility.filter.ValidateUser;
import com.strandls.authentication_utility.util.AuthUtil;
import com.strandls.cca.ApiConstants;
import com.strandls.cca.exception.CCAException;
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.CCALocation;
import com.strandls.cca.pojo.EncryptedKey;
import com.strandls.cca.pojo.Follower;
import com.strandls.cca.pojo.Permission;
import com.strandls.cca.pojo.UsergroupCCA;
import com.strandls.cca.pojo.response.AggregationResponse;
import com.strandls.cca.pojo.response.SubsetCCADataList;
import com.strandls.cca.service.CCADataService;
import com.strandls.cca.util.AuthorizationUtil;
import com.strandls.cca.util.Permissions;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("CCA Data Services")
@Path(ApiConstants.V1 + ApiConstants.DATA)
public class CCADataController {

	@Inject
	private CCADataService ccaDataService;

	@GET
	@Path("/ping")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)

	@ApiOperation(value = "Ping pong", notes = "", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "", response = String.class) })

	public Response ping() throws CCAException {
		try {
			return Response.status(Status.OK).entity("Pong").build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@GET
	@Path("{id}")

	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get the cca data", notes = "Returns CCA data fields", response = CCAData.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not get the data", response = String.class) })
	public Response getCCAData(@Context HttpServletRequest request, @PathParam("id") Long id,
			@QueryParam("language") String language) throws CCAException {
		try {
			CCAData ccaData = ccaDataService.findById(id, language);
			if (ccaData == null)
				throw new ObjectNotFoundException(id, id.toString());
			return Response.status(Status.OK).entity(ccaData).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@GET
	@Path("/summary/{id}")

	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get the cca data", notes = "Returns CCA data fields", response = CCAData.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not get the data", response = String.class) })
	public Response getCCADataSummary(@Context HttpServletRequest request, @PathParam("id") Long id,
			@QueryParam("language") String language) throws CCAException {
		try {
			SubsetCCADataList subsetCCAData = ccaDataService.getSummaryData(id, language);
			if (subsetCCAData == null)
				throw new ObjectNotFoundException(id, id.toString());
			return Response.status(Status.OK).entity(subsetCCAData).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@GET
	@Path("/myList")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get the cca data contributed by me", notes = "Returns CCA data contributed by me", response = AggregationResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not get the data", response = String.class) })
	public Response getMyCCADataList(@Context HttpServletRequest request, @Context UriInfo uriInfo)
			throws CCAException {
		try {
			return Response.status(Status.OK).entity(ccaDataService.getMyCCADataList(request, uriInfo)).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@GET
	@Path("/list")

	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get the cca data", notes = "Returns CCA data fields", response = AggregationResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not get the data", response = String.class) })
	public Response getCCADataList(@Context HttpServletRequest request, @Context UriInfo uriInfo) throws CCAException {
		try {
			return Response.status(Status.OK).entity(ccaDataService.getCCADataList(request, uriInfo, false)).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@GET
	@Path("/chart")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Search CCA Chart data", notes = "Returns CCA Chart data based on the search query", response = CCAData.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not get the data", response = String.class) })
	public Response searchChartData(@QueryParam("query") String query, @Context HttpServletRequest request,
			@Context UriInfo uriInfo) throws CCAException {
		try {
			return Response.status(Status.OK).entity(ccaDataService.searchChartData(query, request, uriInfo)).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@GET
	@Path("/aggregation")

	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get the cca data", notes = "Returns CCA data fields", response = AggregationResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not get the data", response = String.class) })
	public Response getAggregateCCADataList(@QueryParam("query") String query, @Context HttpServletRequest request,
			@Context UriInfo uriInfo) throws CCAException {
		try {
			return Response.status(Status.OK)
					.entity(ccaDataService.getCCADataAggregation(query, request, uriInfo, false)).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@GET
	@Path("/map/info")

	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get the cca data", notes = "Returns CCA data fields", response = AggregationResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not get the data", response = String.class) })
	public Response getCCADataMapInfo(@Context HttpServletRequest request, @Context UriInfo uriInfo)
			throws CCAException {
		try {
			return Response.status(Status.OK).entity(ccaDataService.getCCAMapData(request, uriInfo, false)).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@GET
	@Path("/page")

	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get the cca data", notes = "Returns CCA data fields", response = AggregationResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not get the data", response = String.class) })
	public Response getCCAData(@Context HttpServletRequest request, @Context UriInfo uriInfo) throws CCAException {
		try {
			return Response.status(Status.OK).entity(ccaDataService.getCCAPageData(request, uriInfo, false)).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@GET
	@Path("/all")

	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get the cca data", notes = "Returns CCA data along with all the fields", response = CCAData.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not get the data", response = String.class) })
	public Response getCCADataDump(@Context HttpServletRequest request, @Context UriInfo uriInfo) throws CCAException {
		try {
			return Response.status(Status.OK).entity(ccaDataService.getAllCCAData(request, uriInfo, false)).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@POST
	@Path("/all/download")

	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Download the cca data", notes = "Downloads CCA data along with all the fields", response = CCAData.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not get the data", response = String.class) })
	public Response downloadCCAData(@Context HttpServletRequest request, @Context UriInfo uriInfo) throws CCAException {
		try {
			return Response.status(Status.OK).entity(ccaDataService.downloadCCAData(request, uriInfo, false)).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@PUT
	@Path("/update")

	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Update the cca data", notes = "Returns CCA data fields", response = CCAData.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not save the data", response = String.class) })

	public Response updateCCAData(@Context HttpServletRequest request, @ApiParam("ccaData") CCAData ccaData)
			throws CCAException {
		try {
			CCAData originalDocs = ccaDataService.findById(ccaData.getId(), null);
			AuthorizationUtil.checkAuthorization(request,
					Arrays.asList(Permissions.ROLE_ADMIN, Permissions.ROLE_DATACURATOR), originalDocs.getUserId(),
					originalDocs);
			return Response.status(Status.OK).entity(ccaDataService.update(request, ccaData, "Data")).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@POST
	@Path("/save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser
	@ApiOperation(value = "Save the cca data", notes = "Returns CCA data fields", response = CCAData.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not save the data", response = String.class) })

	public Response saveCCAData(@Context HttpServletRequest request, @ApiParam("ccaData") CCAData ccaData)
			throws CCAException {
		try {
			return Response.status(Status.OK).entity(ccaDataService.save(request, ccaData)).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@POST
	@Path("/upload")
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser
	@ApiOperation(value = "Upload cca data from the file", notes = "Returns CCA data list", response = CCAData.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not save the data", response = String.class) })

	public Response uploadCCADataFromFile(@Context HttpServletRequest request, final FormDataMultiPart multiPart)
			throws CCAException {
		try {
			AuthorizationUtil.handleAuthorization(request, Arrays.asList(Permissions.ROLE_ADMIN), null);
			return Response.status(Status.OK).entity(ccaDataService.uploadCCADataFromFile(request, multiPart)).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@PUT
	@Path("/restore/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser
	@ApiOperation(value = "Delete the cca data (Mark as read)", notes = "Returns CCA Deleted cca", response = CCAData.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not delete the data", response = String.class) })

	public Response restoreCCAData(@Context HttpServletRequest request, @PathParam("id") Long id) throws CCAException {
		try {
			AuthorizationUtil.handleAuthorization(request,
					Arrays.asList(Permissions.ROLE_ADMIN, Permissions.ROLE_DATACURATOR), null);
			return Response.status(Status.OK).entity(ccaDataService.restore(id)).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@PUT
	@Path("/update/permission")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser
	@ApiOperation(value = "Update the cca data permission", notes = "Returns CCA data fields with permission info", response = CCAData.class)
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Could not save permission data", response = String.class) })

	public Response updatePermissionCCAData(@Context HttpServletRequest request,
			@ApiParam("permission") Permission permission) throws CCAException {
		try {
			CCAData originalDocs = ccaDataService.findById(permission.getId(), null);
			AuthorizationUtil.handleAuthorization(request,
					Arrays.asList(Permissions.ROLE_ADMIN, Permissions.ROLE_DATACURATOR), originalDocs.getUserId());
			Set<String> s = new HashSet<>();
			s.addAll(permission.getAllowedUsers());
			originalDocs.setAllowedUsers(s);
			return Response.status(Status.OK).entity(ccaDataService.update(request, originalDocs, "Permission"))
					.build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@PUT
	@Path(ApiConstants.UPDATE + ApiConstants.USERGROUP)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser
	@ApiOperation(value = "Update usergroup for cca data", notes = "Returns CCA data fields with usergroup info", response = CCAData.class)
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Could not save usergroup data", response = String.class) })

	public Response updateUsergroupCCAData(@Context HttpServletRequest request,
			@ApiParam("usergroup") UsergroupCCA usergroup) throws CCAException {
		try {
			CCAData originalDocs = ccaDataService.findById(usergroup.getId(), null);
			Set<String> groups = new HashSet<>();
			groups.addAll(usergroup.getUsergroups());
			originalDocs.setUsergroups(groups);
			return Response.status(Status.OK).entity(ccaDataService.update(request, originalDocs, "UpdateUsergroup"))
					.build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@PUT
	@Path(ApiConstants.UPDATE + ApiConstants.LOCATION)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser
	@ApiOperation(value = "Update location for cca data", notes = "Returns CCA data fields with location info", response = CCAData.class)
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Could not save location data", response = String.class) })

	public Response updateLocationCCAData(@Context HttpServletRequest request,
			@ApiParam("usergroup") CCALocation location) throws CCAException {
		try {
			CCAData originalDocs = ccaDataService.findById(location.getId(), null);
			AuthorizationUtil.handleAuthorization(request,
					Arrays.asList(Permissions.ROLE_ADMIN, Permissions.ROLE_DATACURATOR), originalDocs.getUserId());

			originalDocs.setLocation(location.getLocation());
			return Response.status(Status.OK).entity(ccaDataService.update(request, originalDocs, "location")).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@PUT
	@Path(ApiConstants.UPDATE + ApiConstants.BULK + ApiConstants.USERGROUP)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser
	@ApiOperation(value = "Update usergroup for List of cca data", notes = "Returns List of CCA data fields with usergroup info", response = CCAData.class)
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Could not save usergroup data", response = String.class) })

	public Response updateBulkUsergroupCCAData(@Context HttpServletRequest request,
			@ApiParam("usergroup") List<UsergroupCCA> usergroups) throws CCAException {
		try {
			List<CCAData> updatedDataList = new ArrayList<>();
			for (UsergroupCCA usergroup : usergroups) {
				CCAData originalDocs = ccaDataService.findById(usergroup.getId(), null);
				Set<String> groups = new HashSet<>();
				groups.addAll(usergroup.getUsergroups());
				originalDocs.setUsergroups(groups);
				CCAData updatedData = ccaDataService.update(request, originalDocs, "UpdateUsergroup");
				updatedDataList.add(updatedData);
			}
			return Response.status(Status.OK).entity(updatedDataList).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@PUT
	@Path("/update/followers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser
	@ApiOperation(value = "Update the followers cca data", notes = "Returns CCA data fields with follower info", response = CCAData.class)
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Could not save follower data", response = String.class) })

	public Response updateCCADataFollowers(@Context HttpServletRequest request,
			@ApiParam("followers") Follower follower) throws CCAException {
		try {
			CCAData originalDocs = ccaDataService.findById(follower.getId(), null);
			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			Set<String> s = new HashSet<>();
			if (follower.getfollowers() != null && !follower.getfollowers().isEmpty())
				s.addAll(follower.getfollowers());
			else
				s.add(profile.getId());

			originalDocs.setFollowers(s);
			String type = follower.getType() == null || follower.getType().equalsIgnoreCase("follow") ? "Follow"
					: "Unfollow";
			return Response.status(Status.OK).entity(ccaDataService.update(request, originalDocs, type)).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@DELETE
	@Path("/delete/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser
	@ApiOperation(value = "Delete the cca data (Mark as read)", notes = "Returns CCA Deleted cca", response = CCAData.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not delete the data", response = String.class) })

	public Response removeCCAData(@Context HttpServletRequest request, @PathParam("id") Long id) throws CCAException {
		try {
			CCAData originalDocs = ccaDataService.findById(id, null);
			AuthorizationUtil.handleAuthorization(request,
					Arrays.asList(Permissions.ROLE_ADMIN, Permissions.ROLE_DATACURATOR), originalDocs.getUserId());
			return Response.status(Status.OK).entity(ccaDataService.remove(request, id)).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@DELETE
	@Path("/delete/deep/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser
	@ApiOperation(value = "Delete the cca data completely", notes = "Returns CCA Deleted cca", response = CCAData.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not delete the data", response = String.class) })

	public Response deepRemoveCCAData(@Context HttpServletRequest request, @PathParam("id") Long id)
			throws CCAException {
		try {
			AuthorizationUtil.handleAuthorization(request, Arrays.asList(Permissions.ROLE_ADMIN), null);
			return Response.status(Status.OK).entity(ccaDataService.deepRemove(id)).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@POST
	@Path(ApiConstants.COMMENT)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Adds a comment", notes = "Return the current activity", response = Activity.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Unable to log a comment", response = String.class) })

	public Response addComment(@Context HttpServletRequest request,
			@ApiParam(name = "commentData") CommentLoggingData commentData) {
		try {
			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			Long userId = Long.parseLong(profile.getId());
			if (commentData.getBody().trim().length() > 0) {
				return Response.status(Status.OK)
						.entity(ccaDataService.addComment(request, userId, commentData.getRootHolderId(), commentData))
						.build();
			}
			return Response.status(Status.NOT_ACCEPTABLE).entity("Blank Comment Not allowed").build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.REQUEST)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser
	@ApiOperation(value = "Send request for permission over a ccaData", notes = "sends mail to the permission", response = Boolean.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to send the req", response = String.class) })

	public Response requestPermission(@Context HttpServletRequest request,
			@ApiParam(name = "permissionData") CcaPermission permissionData) {
		try {
			if (permissionData != null) {
				Long ccaId = permissionData.getCcaid();
				CCAData originalDocs = ccaDataService.findById(ccaId, null);

				Boolean result = ccaDataService.sendPermissionRequest(request, permissionData, originalDocs);
				if (result != null) {
					if (result)
						return Response.status(Status.OK).entity(result).build();
					return Response.status(Status.NOT_MODIFIED).build();
				}

			}

			return Response.status(Status.NOT_FOUND).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.GRANT)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "validate the request for permission over a ccaData", notes = "checks the grants the permission", response = Boolean.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "uable to grant the permission", response = String.class) })

	public Response grantPermissionrequest(@Context HttpServletRequest request,
			@ApiParam(name = "encryptedKey") EncryptedKey encryptedKey) {
		try {
			AuthorizationUtil.handleAuthorization(request,
					Arrays.asList(Permissions.ROLE_ADMIN, Permissions.ROLE_DATACURATOR), null);
			return Response.status(Status.OK).entity(ccaDataService.sendPermissionGrant(request, encryptedKey)).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.SEARCH)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Search CCA data", notes = "Returns CCA data based on the search query", response = CCAData.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not get the data", response = String.class) })
	public Response searchCCAData(@QueryParam("query") String query, @Context HttpServletRequest request,
			@Context UriInfo uriInfo) throws CCAException {
		try {
			return Response.status(Status.OK).entity(ccaDataService.searchCCAData(query, request, uriInfo)).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@GET
	@Path(ApiConstants.SEARCH + ApiConstants.MAP)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Search CCA data", notes = "Returns CCA data based on the search query", response = CCAData.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not get the data", response = String.class) })
	public Response searchmapCCAData(@QueryParam("query") String query, @Context HttpServletRequest request,
			@Context UriInfo uriInfo) throws CCAException {
		try {
			return Response.status(Status.OK).entity(ccaDataService.searchMapCCAData(query, request, uriInfo)).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@POST
	@Path(ApiConstants.BULK_UPLOAD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser
	@ApiOperation(value = "Save multiple CCA data", notes = "Saves multiple CCA data entries", response = CCAData.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not save the data", response = String.class) })
	public Response saveBulkCCAData(@Context HttpServletRequest request, List<CCAData> ccaDataList)
			throws CCAException {
		try {
			List<CCAData> savedDataList = ccaDataService.saveCCADataInBulk(request, ccaDataList);
			return Response.status(Status.OK).entity(savedDataList).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

}