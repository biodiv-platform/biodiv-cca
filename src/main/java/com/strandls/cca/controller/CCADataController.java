package com.strandls.cca.controller;

import java.util.Arrays;
import java.util.List;

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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com.strandls.authentication_utility.filter.ValidateUser;
import com.strandls.cca.ApiConstants;
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.response.AggregationResponse;
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

	public Response ping() {
		try {
			return Response.status(Status.OK).entity("Pong").build();
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(
					Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build());
		} catch (Exception e) {
			throw new WebApplicationException(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
	}

	@GET
	@Path("{id}")

	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get the cca data", notes = "Returns CCA data fields", response = CCAData.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not get the data", response = String.class) })
	public Response getCCAData(@Context HttpServletRequest request, @PathParam("id") Long id) {
		try {
			CCAData ccaData = ccaDataService.findById(id);
			return Response.status(Status.OK).entity(ccaData).build();
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(
					Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build());
		} catch (Exception e) {
			throw new WebApplicationException(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
	}

	@GET
	@Path("/myList")
	@ValidateUser
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get the cca data contributed by me", notes = "Returns CCA data contributed by me", response = AggregationResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not get the data", response = String.class) })
	public Response getMyCCADataList(@Context HttpServletRequest request, @Context UriInfo uriInfo) {
		try {
			AggregationResponse ccaData = ccaDataService.getMyCCADataList(request, uriInfo);
			return Response.status(Status.OK).entity(ccaData).build();
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(
					Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build());
		} catch (Exception e) {
			throw new WebApplicationException(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
	}

	@GET
	@Path("/list")

	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get the cca data", notes = "Returns CCA data fields", response = AggregationResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not get the data", response = String.class) })
	public Response getCCADataList(@Context HttpServletRequest request, @Context UriInfo uriInfo) {
		try {
			AggregationResponse ccaData = ccaDataService.getCCADataList(request, uriInfo, false);
			return Response.status(Status.OK).entity(ccaData).build();
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(
					Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build());
		} catch (Exception e) {
			throw new WebApplicationException(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
	}

	@GET
	@Path("/all")

	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get the cca data", notes = "Returns CCA data along with all the fields", response = CCAData.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not get the data", response = String.class) })
	public Response getCCADataDump(@Context HttpServletRequest request, @Context UriInfo uriInfo) {
		try {
			List<CCAData> ccaData = ccaDataService.getAllCCAData(request, uriInfo);
			return Response.status(Status.OK).entity(ccaData).build();
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

	@ApiOperation(value = "Update the cca data", notes = "Returns CCA data fields", response = CCAData.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not save the data", response = String.class) })

	public Response updateCCAData(@Context HttpServletRequest request, @ApiParam("ccaData") CCAData ccaData) {
		try {
			if (AuthorizationUtil.checkAuthorization(request,
					Arrays.asList(Permissions.ROLE_ADMIN, Permissions.ROLE_DATACURATOR), ccaData.getId())) {
				ccaData = ccaDataService.update(request, ccaData);
				return Response.status(Status.OK).entity(ccaData).build();
			} else {
				throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
						.entity(AuthorizationUtil.UNAUTHORIZED_MESSAGE).build());
			}
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

	@ApiOperation(value = "Save the cca data", notes = "Returns CCA data fields", response = CCAData.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not save the data", response = String.class) })

	public Response saveCCAData(@Context HttpServletRequest request, @ApiParam("ccaData") CCAData ccaData) {
		try {
			ccaData = ccaDataService.save(request, ccaData);
			return Response.status(Status.OK).entity(ccaData).build();
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(
					Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build());
		} catch (Exception e) {
			throw new WebApplicationException(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
	}

	@POST
	@Path("/upload")

	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Upload cca data from the file", notes = "Returns CCA data list", response = CCAData.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not save the data", response = String.class) })

	public Response uploadCCADataFromFile(@Context HttpServletRequest request, final FormDataMultiPart multiPart) {
		try {
			if (AuthorizationUtil.checkAuthorization(request, Arrays.asList(Permissions.ROLE_ADMIN), null)) {
				List<CCAData> ccaData = ccaDataService.uploadCCADataFromFile(request, multiPart);
				return Response.status(Status.OK).entity(ccaData).build();
			} else {
				throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
						.entity(AuthorizationUtil.UNAUTHORIZED_MESSAGE).build());
			}
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(
					Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build());
		} catch (Exception e) {
			throw new WebApplicationException(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
	}

	@PUT
	@Path("/restore/{id}")

	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Delete the cca data (Mark as read)", notes = "Returns CCA Deleted cca", response = CCAData.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not delete the data", response = String.class) })

	public Response restoreCCAData(@Context HttpServletRequest request, @PathParam("id") Long id) {
		try {
			if (AuthorizationUtil.checkAuthorization(request,
					Arrays.asList(Permissions.ROLE_ADMIN, Permissions.ROLE_DATACURATOR), id)) {
				CCAData ccaData = ccaDataService.restore(id);
				return Response.status(Status.OK).entity(ccaData).build();
			} else {
				throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
						.entity(AuthorizationUtil.UNAUTHORIZED_MESSAGE).build());
			}
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(
					Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build());
		} catch (Exception e) {
			throw new WebApplicationException(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
	}

	@DELETE
	@Path("/delete/{id}")

	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Delete the cca data (Mark as read)", notes = "Returns CCA Deleted cca", response = CCAData.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not delete the data", response = String.class) })

	public Response removeCCAData(@Context HttpServletRequest request, @PathParam("id") Long id) {
		try {
			if (AuthorizationUtil.checkAuthorization(request,
					Arrays.asList(Permissions.ROLE_ADMIN, Permissions.ROLE_DATACURATOR), id)) {
				CCAData ccaData = ccaDataService.remove(id);
				return Response.status(Status.OK).entity(ccaData).build();
			} else {
				throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
						.entity(AuthorizationUtil.UNAUTHORIZED_MESSAGE).build());
			}
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(
					Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build());
		} catch (Exception e) {
			throw new WebApplicationException(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
	}

	@DELETE
	@Path("/delete/deep/{id}")

	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Delete the cca data completely", notes = "Returns CCA Deleted cca", response = CCAData.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Could not delete the data", response = String.class) })

	public Response deepRemoveCCAData(@Context HttpServletRequest request, @PathParam("id") Long id) {
		try {
			if (AuthorizationUtil.checkAuthorization(request, Arrays.asList(Permissions.ROLE_ADMIN), null)) {
				CCAData ccaData = ccaDataService.deepRemove(id);
				return Response.status(Status.OK).entity(ccaData).build();
			} else {
				throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
						.entity(AuthorizationUtil.UNAUTHORIZED_MESSAGE).build());
			}
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(
					Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build());
		} catch (Exception e) {
			throw new WebApplicationException(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
	}
}
