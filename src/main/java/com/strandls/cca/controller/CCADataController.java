package com.strandls.cca.controller;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.strandls.cca.ApiConstants;
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.CCATemplate;
import com.strandls.cca.service.CCADataService;

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

	public Response ping() {
		try {
			return Response.status(Status.OK).entity("Pong").build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Save the cca data", notes = "Returns CCA data fields", response = CCATemplate.class)
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Could not save the data", response = String.class) })

	public Response saveCCATemplate(@ApiParam("ccaData") CCAData ccaData) {
		try {
			ccaData = ccaDataService.saveOrUpdate(ccaData);
			return Response.status(Status.OK).entity(ccaData).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Delete the cca data", notes = "Returns CCA Deleted cca", response = CCATemplate.class)
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Could not delete the data", response = String.class) })

	public Response removeCCATemplate(@ApiParam("ccaData") CCAData ccaData) {
		try {
			ccaData = ccaDataService.remove(ccaData);
			return Response.status(Status.OK).entity(ccaData).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
}
