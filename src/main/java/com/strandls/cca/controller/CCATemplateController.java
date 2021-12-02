package com.strandls.cca.controller;

import java.util.List;

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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.strandls.authentication_utility.filter.ValidateUser;
import com.strandls.cca.ApiConstants;
import com.strandls.cca.pojo.CCATemplate;
import com.strandls.cca.pojo.Platform;
import com.strandls.cca.service.CCATemplateService;

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
	@Path("/all")

	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Find CCA METADATA by ID", notes = "Returns CCA field details", response = CCATemplate.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "CCA field not found", response = String.class) })

	public Response getAllCCATemplate(@Context HttpServletRequest request, @QueryParam("platform") Platform plateform,
			@QueryParam("language") String language) {
		try {
			List<CCATemplate> ccaTemplate = ccaContextService.getAllCCATemplate(request, plateform, language);
			return Response.status(Status.OK).entity(ccaTemplate).build();
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(
					Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build());
		} catch (Exception e) {
			throw new WebApplicationException(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
	}

	@GET
	@Path("/{shortName}")

	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Find CCA METADATA by ID", notes = "Returns CCA field details", response = CCATemplate.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "CCA field not found", response = String.class) })

	public Response getCCATemplateById(@Context HttpServletRequest request, @PathParam("shortName") String shortName,
			@DefaultValue("en") @QueryParam("language") String language) {
		try {
			CCATemplate ccaTemplate = ccaContextService.getCCAByShortName(shortName, language);
			return Response.status(Status.OK).entity(ccaTemplate).build();
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(
					Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build());
		} catch (Exception e) {
			throw new WebApplicationException(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
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
			@ApiParam("ccaTemplate") CCATemplate ccaMasterField) {
		try {
			ccaMasterField = ccaContextService.save(request, ccaMasterField);
			return Response.status(Status.OK).entity(ccaMasterField).build();
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(
					Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build());
		} catch (Exception e) {
			throw new WebApplicationException(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
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
			@ApiParam("ccaTemplate") CCATemplate ccaMasterField) {
		try {
			ccaMasterField = ccaContextService.update(request, ccaMasterField);
			return Response.status(Status.OK).entity(ccaMasterField).build();
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(
					Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build());
		} catch (Exception e) {
			throw new WebApplicationException(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
	}

	@PUT
	@Path("/revoke/{shortName}")

	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Delete the cca template(Mark as deleted)", notes = "Returns delelted CCA Template", response = CCATemplate.class)
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Could not delete the CCA Template", response = String.class) })

	public Response revokeCCATemplate(@Context HttpServletRequest request, @PathParam("shortName") String shortName) {
		try {
			CCATemplate ccaMasterField = ccaContextService.revoke(request, shortName);
			return Response.status(Status.OK).entity(ccaMasterField).build();
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(
					Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build());
		} catch (Exception e) {
			throw new WebApplicationException(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
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

	public Response removeCCATemplate(@Context HttpServletRequest request, @PathParam("shortName") String shortName) {
		try {
			CCATemplate ccaMasterField = ccaContextService.remove(request, shortName);
			return Response.status(Status.OK).entity(ccaMasterField).build();
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(
					Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build());
		} catch (Exception e) {
			throw new WebApplicationException(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
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
			@PathParam("shortName") String shortName) {
		try {
			CCATemplate ccaMasterField = ccaContextService.deepRemove(request, shortName);
			return Response.status(Status.OK).entity(ccaMasterField).build();
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(
					Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build());
		} catch (Exception e) {
			throw new WebApplicationException(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
	}

}
