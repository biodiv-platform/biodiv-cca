package com.strandls.cca.controller;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.strandls.cca.ApiConstants;
import com.strandls.cca.pojo.CCAMetaData;
import com.strandls.cca.service.CCAMetaDataService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("CCA Services")
@Path(ApiConstants.V1 + ApiConstants.METADATA)
public class CCAMetaDataController {

	@Inject
	private CCAMetaDataService ccaMetaDataService;
	
	@GET
	@Path("/{id}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find CCA METADATA by ID", notes = "Returns CCA field details", response = Long.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "CCA field not found", response = String.class) })

	public Response getCCAMetaDataById(@PathParam("id") Long ccaId) {
		try {
			CCAMetaData ccaMetaData = ccaMetaDataService.getCCAMetaDataById(ccaId);
			return Response.status(Status.OK).entity(ccaMetaData).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Save the cca metadata", notes = "Returns CCA Metadata fields", response = Long.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not save the metadata", response = String.class) })

	public Response saveCCAMetaData(@ApiParam("ccaMetaData") CCAMetaData ccaMetaData) {
		try {
			ccaMetaData = ccaMetaDataService.saveCCAMetaData(ccaMetaData);
			return Response.status(Status.OK).entity(ccaMetaData).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
}
