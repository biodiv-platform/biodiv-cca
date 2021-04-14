package com.strandls.cca.controller;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.strandls.cca.ApiConstants;
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.service.CCADataService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("CCA Services")
@Path(ApiConstants.V1 + ApiConstants.METADATA)
public class CCAMetaDataController {

	@Inject
	private CCADataService ccaService;
	
	@GET
	@Path("/{id}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find CCA METADATA by ID", notes = "Returns CCA field details", response = Long.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "CCA field not found", response = String.class) })

	public Response getCCAById(@PathParam("id") Long ccaId) {
		try {
			CCAData cca = ccaService.getCCADataById(ccaId);
			return Response.status(Status.OK).entity(cca).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
}
