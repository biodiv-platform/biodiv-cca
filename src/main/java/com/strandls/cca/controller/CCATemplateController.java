package com.strandls.cca.controller;

import java.util.Arrays;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
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

import org.pac4j.core.profile.CommonProfile;

import com.strandls.activity.pojo.Activity;
import com.strandls.activity.pojo.CommentLoggingData;
import com.strandls.authentication_utility.filter.ValidateUser;
import com.strandls.authentication_utility.util.AuthUtil;
import com.strandls.cca.ApiConstants;
import com.strandls.cca.exception.CCAException;
import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCATemplate;
import com.strandls.cca.pojo.Platform;
import com.strandls.cca.service.CCATemplateService;
import com.strandls.cca.util.AuthorizationUtil;
import com.strandls.cca.util.Permissions;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("CCA Template Services")
@Path(ApiConstants.V1 + ApiConstants.TEMPLATE)
public class CCATemplateController {

	@Inject
	private CCATemplateService ccaContextService;

	@GET
	@Path("/ping")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)

	@ApiOperation(value = "Ping pong", notes = "", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "", response = String.class) })

	public Response ping() {
		try {
			return Response.status(Status.OK).entity("Pong").build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@GET
	@Path("/filter/fields")

	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find CCA METADATA by ID", notes = "Returns all filterable CCA fields", response = CCAField.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "CCA field not found", response = String.class) })

	public Response getFilterableFields(@Context HttpServletRequest request, @QueryParam("shortName") String shortName,
			@QueryParam("language") String language) throws CCAException {
		try {
			return Response.status(Status.OK).entity(ccaContextService.getFilterableFields(request, shortName, language)).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@GET
	@Path("/all")

	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Find CCA METADATA by ID", notes = "Returns CCA field details", response = CCATemplate.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "CCA field not found", response = String.class) })

	public Response getAllCCATemplate(@Context HttpServletRequest request, @QueryParam("platform") Platform plateform,
			@QueryParam("language") String language,
			@DefaultValue("true") @QueryParam("excludeFields") Boolean excludeFields) throws CCAException {
		try {
			return Response.status(Status.OK).entity(ccaContextService.getAllCCATemplate(request, plateform, 
					language, excludeFields)).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@GET
	@Path("/{shortName}")

	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find CCA METADATA by ID", notes = "Returns CCA field details", response = CCATemplate.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "CCA field not found", response = String.class) })

	public Response getCCATemplateById(@Context HttpServletRequest request, @PathParam("shortName") String shortName,
			@DefaultValue("en") @QueryParam("language") String language, 
			@DefaultValue("false") @QueryParam("isDeleted") boolean isDeleted) throws CCAException {
		try {
			CCATemplate ccaTemplate = ccaContextService.getCCAByShortName(shortName, language, isDeleted);
			return Response.status(Status.OK).entity(ccaTemplate == null ? "Not found deleted template with short name : "+ shortName : ccaTemplate).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}
	
	@POST
	@Path("/save")

	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Create the cca template", notes = "Returns CCA template created fields", response = CCATemplate.class)
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Could not create the CCA template", response = String.class) })

	public Response createCCATemplate(@Context HttpServletRequest request,
			@ApiParam("ccaTemplate") CCATemplate ccaTemplate) throws CCAException {
		try {
			AuthorizationUtil.handleAuthorization(request,
					Arrays.asList(Permissions.ROLE_ADMIN, Permissions.ROLE_TEMPLATECURATOR), null);
			return Response.status(Status.OK).entity(ccaContextService.save(request, ccaTemplate)).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@GET
	@Path("/pullMasterTranslation")

	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Update translation from master template", notes = "Returns CCA Template and its fields", response = CCATemplate.class)
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Could not update the CCA Template translation", response = String.class) })

	public Response pullTranslationFromMaster(@Context HttpServletRequest request,
			@QueryParam("templateId") Long templateId, @QueryParam("language") String language) throws CCAException {
		try {
			AuthorizationUtil.handleAuthorization(request,
					Arrays.asList(Permissions.ROLE_ADMIN, Permissions.ROLE_TEMPLATECURATOR), null);
			return Response.status(Status.OK).entity(ccaContextService.pullTranslationFromMaster(request, templateId, language)).build();			
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@PUT
	@Path("/update")

	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Update the cca template", notes = "Returns CCA Template and its fields", response = CCATemplate.class)
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Could not save the CCA Template", response = String.class) })

	public Response updateCCATemplate(@Context HttpServletRequest request,
			@ApiParam("ccaTemplate") CCATemplate ccaTemplate) throws CCAException {
		try {
			AuthorizationUtil.handleAuthorization(request,
					Arrays.asList(Permissions.ROLE_ADMIN, Permissions.ROLE_TEMPLATECURATOR), null);
			return Response.status(Status.OK).entity(ccaContextService.update(request, ccaTemplate)).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@PUT
	@Path("/restore/{shortName}")

	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Restore the cca template(Which was marked as deleted)", notes = "Returns deleted CCA Template", response = CCATemplate.class)
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Could not delete the CCA Template", response = String.class) })

	public Response restoreCCATemplate(@Context HttpServletRequest request, @PathParam("shortName") String shortName) throws CCAException {
		try {
			AuthorizationUtil.handleAuthorization(request,
					Arrays.asList(Permissions.ROLE_ADMIN, Permissions.ROLE_TEMPLATECURATOR), null);
			return Response.status(Status.OK).entity(ccaContextService.restore(request, shortName)).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@DELETE
	@Path("/delete/{shortName}")

	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Delete the cca template(Mark as deleted)", notes = "Returns delelted CCA Template", response = CCATemplate.class)
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Could not delete the CCA Template", response = String.class) })

	public Response removeCCATemplate(@Context HttpServletRequest request, @PathParam("shortName") String shortName) throws CCAException {
		try {
			AuthorizationUtil.handleAuthorization(request,
					Arrays.asList(Permissions.ROLE_ADMIN, Permissions.ROLE_TEMPLATECURATOR), null);
			CCATemplate ccaMasterField = ccaContextService.remove(request, shortName);
			return Response.status(Status.OK).entity(ccaMasterField).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}

	@DELETE
	@Path("/delete/deep/{shortName}")

	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Delete the cca template completely", notes = "Returns delelted CCA Template", response = CCATemplate.class)
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Could not delete the CCA Template", response = String.class) })

	public Response deepRemoveCCATemplate(@Context HttpServletRequest request,
			@PathParam("shortName") String shortName) throws CCAException {
		try {
			AuthorizationUtil.handleAuthorization(request, Arrays.asList(Permissions.ROLE_ADMIN), null);
			return Response.status(Status.OK).entity(ccaContextService.deepRemove(request, shortName)).build();
		} catch (Exception e) {
			throw new CCAException(e);
		}
	}
	
	@POST
	@Path(ApiConstants.COMMENT + "/{shortName}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Adds a comment", notes = "Return the current activity", response = Activity.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Unable to log a comment", response = String.class) })

	public Response addComment(@Context HttpServletRequest request,
			@ApiParam(name = "commentData") CommentLoggingData commentData,
			@PathParam("shortName") String shortName) {
		try {

			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			Long userId = Long.parseLong(profile.getId());
			if (commentData.getBody().trim().length() > 0) {
				return Response.status(Status.OK).entity(ccaContextService.addComment(request, userId, shortName, commentData)).build();
			}
			return Response.status(Status.NOT_ACCEPTABLE).entity("Blank Comment Not allowed").build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
}
