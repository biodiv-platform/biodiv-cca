package com.strandls.cca.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "CCA Data Services")
@Path(ApiConstants.V1 + ApiConstants.DATA)
public class CCADataController {

    @Inject
    private CCADataService ccaDataService;

    @GET
    @Path("/ping")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(summary = "Ping pong", description = "Health check endpoint")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "404", description = "Not Found")
    })
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
    @Operation(summary = "Get CCA data", description = "Returns CCA data fields")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "CCA data",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CCAData.class))),
        @ApiResponse(responseCode = "404", description = "Could not get the data")
    })
    public Response getCCAData(
            @Context HttpServletRequest request,
            @Parameter(description = "CCA identifier") @PathParam("id") Long id,
            @Parameter(description = "Language code") @QueryParam("language") String language) throws CCAException {
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
    @Operation(summary = "Get CCA data summary", description = "Returns subset summary for a CCA record")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Summary data",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = SubsetCCADataList.class))),
        @ApiResponse(responseCode = "404", description = "Could not get the data")
    })
    public Response getCCADataSummary(
            @Context HttpServletRequest request,
            @Parameter(description = "CCA identifier") @PathParam("id") Long id,
            @Parameter(description = "Language code") @QueryParam("language") String language) throws CCAException {
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
    @Operation(summary = "My CCA contributions", description = "Returns CCA data contributed by the current user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Aggregation result",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = AggregationResponse.class))),
        @ApiResponse(responseCode = "404", description = "Could not get the data")
    })
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
    @Operation(summary = "List CCA data", description = "Returns paginated list of CCA data")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Aggregation result",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = AggregationResponse.class))),
        @ApiResponse(responseCode = "404", description = "Could not get the data")
    })
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
    @Operation(summary = "Search CCA chart data", description = "Returns chart data based on search query")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Chart data",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                array = @ArraySchema(schema = @Schema(implementation = CCAData.class)))),
        @ApiResponse(responseCode = "404", description = "Could not get the data")
    })
    public Response searchChartData(
            @Parameter(description = "Search query") @QueryParam("query") String query,
            @Context HttpServletRequest request,
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
    @Operation(summary = "Aggregate CCA data", description = "Returns aggregation for query")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Aggregation result",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = AggregationResponse.class))),
        @ApiResponse(responseCode = "404", description = "Could not get the data")
    })
    public Response getAggregateCCADataList(
            @Parameter(description = "Search query") @QueryParam("query") String query,
            @Context HttpServletRequest request,
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
    @Operation(summary = "Map info", description = "Returns map-related data for the current query/page")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Aggregation result",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = AggregationResponse.class))),
        @ApiResponse(responseCode = "404", description = "Could not get the data")
    })
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
    @Operation(summary = "Page data", description = "Returns page-level aggregation for current filters")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Aggregation result",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = AggregationResponse.class))),
        @ApiResponse(responseCode = "404", description = "Could not get the data")
    })
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
    @Operation(summary = "Dump all CCA data", description = "Returns all CCA data with all fields")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "All CCA data",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                array = @ArraySchema(schema = @Schema(implementation = CCAData.class)))),
        @ApiResponse(responseCode = "404", description = "Could not get the data")
    })
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
    @Operation(summary = "Download CCA data", description = "Downloads CCA data along with all the fields")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Download started",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                array = @ArraySchema(schema = @Schema(implementation = CCAData.class)))),
        @ApiResponse(responseCode = "404", description = "Could not get the data")
    })
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
    @Operation(summary = "Update CCA data", description = "Updates existing CCA data")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Updated CCA data",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CCAData.class))),
        @ApiResponse(responseCode = "404", description = "Could not save the data")
    })
    public Response updateCCAData(
            @Context HttpServletRequest request,
            @Parameter(description = "CCA data payload") CCAData ccaData)
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
    @Operation(summary = "Save CCA data", description = "Creates a new CCA data record")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Saved CCA data",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CCAData.class))),
        @ApiResponse(responseCode = "404", description = "Could not save the data")
    })
    public Response saveCCAData(
            @Context HttpServletRequest request,
            @Parameter(description = "CCA data payload") CCAData ccaData)
            throws CCAException {
        try {
            return Response.status(Status.OK).entity(ccaDataService.save(request, ccaData)).build();
        } catch (Exception e) {
            throw new CCAException(e);
        }
    }

    // Multipart upload: describe multipart body explicitly using RequestBody with a DTO
    @POST
    @Path("/upload")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    @Produces(MediaType.APPLICATION_JSON)
    @ValidateUser
    @Operation(summary = "Upload CCA data from file", description = "Uploads a file and returns parsed CCA data list")
    @RequestBody(required = true,
        content = @Content(
            mediaType = MediaType.MULTIPART_FORM_DATA,
            schema = @Schema(implementation = UploadCCADataForm.class),
            encoding = {
                @Encoding(name = "file", contentType = "application/octet-stream")
            }
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Uploaded CCA data list",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                array = @ArraySchema(schema = @Schema(implementation = CCAData.class)))),
        @ApiResponse(responseCode = "404", description = "Could not save the data")
    })
    public Response uploadCCADataFromFile(
            @Context HttpServletRequest request,
            @Parameter(hidden = true) final FormDataMultiPart multiPart)
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
    @Operation(summary = "Restore CCA data", description = "Restores a CCA record marked as deleted")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Restored CCA data",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CCAData.class))),
        @ApiResponse(responseCode = "404", description = "Could not restore the data")
    })
    public Response restoreCCAData(
            @Context HttpServletRequest request,
            @Parameter(description = "CCA identifier") @PathParam("id") Long id) throws CCAException {
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
    @Operation(summary = "Update permissions", description = "Updates permission info for a CCA record")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Updated CCA data with permissions",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CCAData.class))),
        @ApiResponse(responseCode = "404", description = "Could not save permission data")
    })
    public Response updatePermissionCCAData(
            @Context HttpServletRequest request,
            @Parameter(description = "Permission payload") Permission permission) throws CCAException {
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
    @Operation(summary = "Update usergroups", description = "Updates usergroup info for a CCA record")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Updated CCA data with usergroups",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CCAData.class))),
        @ApiResponse(responseCode = "404", description = "Could not save usergroup data")
    })
    public Response updateUsergroupCCAData(
            @Context HttpServletRequest request,
            @Parameter(description = "Usergroup payload") UsergroupCCA usergroup) throws CCAException {
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
    @Operation(summary = "Update location", description = "Updates location info for a CCA record")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Updated CCA data with location",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CCAData.class))),
        @ApiResponse(responseCode = "404", description = "Could not save location data")
    })
    public Response updateLocationCCAData(
            @Context HttpServletRequest request,
            @Parameter(description = "Location payload") CCALocation location) throws CCAException {
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
    @Operation(summary = "Bulk update usergroups", description = "Updates usergroups for a list of CCA records")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Updated list of CCA data",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                array = @ArraySchema(schema = @Schema(implementation = CCAData.class)))),
        @ApiResponse(responseCode = "404", description = "Could not save usergroup data")
    })
    public Response updateBulkUsergroupCCAData(
            @Context HttpServletRequest request,
            @Parameter(description = "List of usergroup updates") List<UsergroupCCA> usergroups) throws CCAException {
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
    @Operation(summary = "Update followers", description = "Adds or removes followers for a CCA record")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Updated CCA data with followers",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CCAData.class))),
        @ApiResponse(responseCode = "404", description = "Could not save follower data")
    })
    public Response updateCCADataFollowers(
            @Context HttpServletRequest request,
            @Parameter(description = "Followers payload") Follower follower) throws CCAException {
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
    @Operation(summary = "Soft delete CCA data", description = "Marks a CCA record as deleted")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Deleted CCA data",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CCAData.class))),
        @ApiResponse(responseCode = "404", description = "Could not delete the data")
    })
    public Response removeCCAData(
            @Context HttpServletRequest request,
            @Parameter(description = "CCA identifier") @PathParam("id") Long id) throws CCAException {
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
    @Operation(summary = "Hard delete CCA data", description = "Permanently deletes a CCA record")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Deleted CCA data",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CCAData.class))),
        @ApiResponse(responseCode = "404", description = "Could not delete the data")
    })
    public Response deepRemoveCCAData(
            @Context HttpServletRequest request,
            @Parameter(description = "CCA identifier") @PathParam("id") Long id)
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
    @Operation(summary = "Add a comment", description = "Adds a comment and returns the activity")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Activity logged",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Activity.class))),
        @ApiResponse(responseCode = "400", description = "Unable to log a comment"),
        @ApiResponse(responseCode = "406", description = "Blank Comment Not allowed")
    })
    public Response addComment(
            @Context HttpServletRequest request,
            @Parameter(name = "commentData", description = "Comment payload") CommentLoggingData commentData) {
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
    @Operation(summary = "Request permission", description = "Sends a permission request over a CCA record")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Request processed",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Boolean.class))),
        @ApiResponse(responseCode = "304", description = "Not modified"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "400", description = "Unable to send the request")
    })
    public Response requestPermission(
            @Context HttpServletRequest request,
            @Parameter(name = "permissionData", description = "Permission request payload") CcaPermission permissionData) {
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
    @Operation(summary = "Grant permission", description = "Validates a request and grants permission")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Permission granted",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Boolean.class))),
        @ApiResponse(responseCode = "400", description = "Unable to grant the permission")
    })
    public Response grantPermissionrequest(
            @Context HttpServletRequest request,
            @Parameter(name = "encryptedKey", description = "Encrypted grant key") EncryptedKey encryptedKey) {
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
    @Operation(summary = "Search CCA data", description = "Returns CCA data based on the search query")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Search result",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                array = @ArraySchema(schema = @Schema(implementation = CCAData.class)))),
        @ApiResponse(responseCode = "404", description = "Could not get the data")
    })
    public Response searchCCAData(
            @Parameter(description = "Search query") @QueryParam("query") String query,
            @Context HttpServletRequest request,
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
    @Operation(summary = "Search CCA data map", description = "Returns CCA data map results based on search query")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Search result",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                array = @ArraySchema(schema = @Schema(implementation = CCAData.class)))),
        @ApiResponse(responseCode = "404", description = "Could not get the data")
    })
    public Response searchmapCCAData(
            @Parameter(description = "Search query") @QueryParam("query") String query,
            @Context HttpServletRequest request,
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
    @Operation(summary = "Save multiple CCA data", description = "Saves multiple CCA data entries")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Saved list",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                array = @ArraySchema(schema = @Schema(implementation = CCAData.class)))),
        @ApiResponse(responseCode = "404", description = "Could not save the data")
    })
    public Response saveBulkCCAData(
            @Context HttpServletRequest request,
            @Parameter(description = "List of CCA data") List<CCAData> ccaDataList)
            throws CCAException {
        try {
            List<CCAData> savedDataList = ccaDataService.saveCCADataInBulk(request, ccaDataList);
            return Response.status(Status.OK).entity(savedDataList).build();
        } catch (Exception e) {
            throw new CCAException(e);
        }
    }

    // Helper DTO for multipart form documentation (OpenAPI 3)
    // Define expected multipart parts (e.g., "file"), additional params can be added as needed.
    public static class UploadCCADataForm {
        @Schema(type = "string", format = "binary", description = "File to upload")
        public byte[] file;
    }
}
