package com.strandls.cca.controller;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.strandls.cca.ApiConstants;
import com.strandls.cca.pojo.CCAContext;
import com.strandls.cca.service.CCAContextService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("CCA Services")
@Path(ApiConstants.V1 + ApiConstants.CONTEXT)
public class CCAContextController {

	@Inject
	private CCAContextService ccaContextService;
	
	@GET
	@Path("/{id}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find CCA METADATA by ID", notes = "Returns CCA field details", response = Long.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "CCA field not found", response = String.class) })

	public Response getCCAContextById(@PathParam("id") String ccaId) {
		try {
			CCAContext ccaContext = ccaContextService.getCCAContextById(ccaId);
			return Response.status(Status.OK).entity(ccaContext).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
	
	@GET
	@Path("/all")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find CCA Registry by ID", notes = "Returns CCA  details", response = CCAContext.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "CCA not found", response = String.class) })

	public Response getAllCCA() {
		try {
			List<CCAContext> ccas = ccaContextService.getAll();
			return Response.status(Status.OK).entity(ccas).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Save the cca metadata", notes = "Returns CCA Metadata fields", response = Long.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not save the metadata", response = String.class) })

	public Response saveCCAContext(@ApiParam("ccaMetaData") CCAContext ccaMasterField) {
		try {
			ccaMasterField = ccaContextService.save(ccaMasterField);
			return Response.status(Status.OK).entity(ccaMasterField).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Update the cca metadata", notes = "Returns CCA  details", response = CCAContext.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "CCA not found", response = String.class) })

	public Response updateCCA(@Context HttpServletRequest request, @ApiParam("cca") CCAContext ccaContext) {
		try {
			ccaContext = ccaContextService.update(ccaContext);
			return Response.status(Status.OK).entity(ccaContext).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
	
	
}
