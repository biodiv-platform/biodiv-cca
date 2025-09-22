package com.strandls.cca.controller;

import java.util.Arrays;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "CCA Template Services")
@Path(ApiConstants.V1 + ApiConstants.TEMPLATE)
public class CCATemplateController {

    @Inject
    private CCATemplateService ccaContextService;

    @GET
    @Path("/ping")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(summary = "Ping pong", description = "Health check endpoint returning Pong")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request")
    })
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
    @Operation(
        summary = "Find CCA metadata",
        description = "Returns all filterable CCA fields for the given template shortName and language"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of filterable fields",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                array = @ArraySchema(schema = @Schema(implementation = CCAField.class)))),
        @ApiResponse(responseCode = "404", description = "CCA field not found")
    })
    public Response getFilterableFields(
            @Context HttpServletRequest request,
            @Parameter(description = "Template short name") @QueryParam("shortName") String shortName,
            @Parameter(description = "Language code") @QueryParam("language") String language) throws CCAException {
        try {
            return Response.status(Status.OK)
                    .entity(ccaContextService.getFilterableFields(request, shortName, language)).build();
        } catch (Exception e) {
            throw new CCAException(e);
        }
    }

    @GET
    @Path("/filter/chart/fields")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Find CCA chart metadata",
        description = "Returns all chartable CCA fields for the given template shortName and language"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of chartable fields",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                array = @ArraySchema(schema = @Schema(implementation = CCAField.class)))),
        @ApiResponse(responseCode = "404", description = "CCA chartable not found")
    })
    public Response getFilterableChartFields(
            @Context HttpServletRequest request,
            @Parameter(description = "Template short name") @QueryParam("shortName") String shortName,
            @Parameter(description = "Language code") @QueryParam("language") String language) throws CCAException {
        try {
            return Response.status(Status.OK)
                    .entity(ccaContextService.getFilterableChartFields(request, shortName, language)).build();
        } catch (Exception e) {
            throw new CCAException(e);
        }
    }

    @GET
    @Path("/all")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @ValidateUser
    @Operation(
        summary = "Find all CCA templates",
        description = "Returns CCA template details filtered by platform and language"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of templates",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                array = @ArraySchema(schema = @Schema(implementation = CCATemplate.class)))),
        @ApiResponse(responseCode = "404", description = "CCA template not found")
    })
    public Response getAllCCATemplate(
            @Context HttpServletRequest request,
            @Parameter(description = "Platform filter") @QueryParam("platform") Platform plateform,
            @Parameter(description = "Language code") @QueryParam("language") String language,
            @Parameter(description = "Exclude field details from response")
            @DefaultValue("true") @QueryParam("excludeFields") Boolean excludeFields) throws CCAException {
        try {
            return Response.status(Status.OK)
                    .entity(ccaContextService.getAllCCATemplate(request, plateform, language, excludeFields)).build();
        } catch (Exception e) {
            throw new CCAException(e);
        }
    }

    @GET
    @Path("/{shortName}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Find CCA metadata by shortName",
        description = "Returns CCA template details or a not-found message if deleted template is requested"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Template details or message",
            content = {
                @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CCATemplate.class)),
                @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class))
            }),
        @ApiResponse(responseCode = "404", description = "CCA template not found")
    })
    public Response getCCATemplateById(
            @Context HttpServletRequest request,
            @Parameter(description = "Template short name") @PathParam("shortName") String shortName,
            @Parameter(description = "Language code, default en")
            @DefaultValue("en") @QueryParam("language") String language,
            @Parameter(description = "Include deleted templates") @DefaultValue("false") @QueryParam("isDeleted") boolean isDeleted)
            throws CCAException {
        try {
            CCATemplate ccaTemplate = ccaContextService.getCCAByShortName(shortName, language, isDeleted);
            return Response.status(Status.OK).entity(
                    ccaTemplate == null ? "Not found deleted template with short name : " + shortName : ccaTemplate)
                    .build();
        } catch (Exception e) {
            throw new CCAException(e);
        }
    }

    @POST
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ValidateUser
    @Operation(
        summary = "Create CCA template",
        description = "Creates a new CCA template and returns created entity"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Created template",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CCATemplate.class))),
        @ApiResponse(responseCode = "404", description = "Could not create the CCA template")
    })
    public Response createCCATemplate(
            @Context HttpServletRequest request,
            @Parameter(description = "CCA template payload") CCATemplate ccaTemplate) throws CCAException {
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
    @Operation(
        summary = "Update translation from master",
        description = "Updates translation from master template for the given templateId and language"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Updated template",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CCATemplate.class))),
        @ApiResponse(responseCode = "404", description = "Could not update the CCA Template translation")
    })
    public Response pullTranslationFromMaster(
            @Context HttpServletRequest request,
            @Parameter(description = "Template ID") @QueryParam("templateId") Long templateId,
            @Parameter(description = "Language code") @QueryParam("language") String language) throws CCAException {
        try {
            AuthorizationUtil.handleAuthorization(request,
                    Arrays.asList(Permissions.ROLE_ADMIN, Permissions.ROLE_TEMPLATECURATOR), null);
            return Response.status(Status.OK)
                    .entity(ccaContextService.pullTranslationFromMaster(request, templateId, language)).build();
        } catch (Exception e) {
            throw new CCAException(e);
        }
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ValidateUser
    @Operation(
        summary = "Update CCA template",
        description = "Updates an existing CCA template and returns updated entity"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Updated template",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CCATemplate.class))),
        @ApiResponse(responseCode = "404", description = "Could not save the CCA Template")
    })
    public Response updateCCATemplate(
            @Context HttpServletRequest request,
            @Parameter(description = "CCA template payload") CCATemplate ccaTemplate) throws CCAException {
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
    @Operation(
        summary = "Restore deleted CCA template",
        description = "Restores a template previously marked as deleted"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Restored template",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CCATemplate.class))),
        @ApiResponse(responseCode = "404", description = "Could not restore the CCA Template")
    })
    public Response restoreCCATemplate(
            @Context HttpServletRequest request,
            @Parameter(description = "Template short name") @PathParam("shortName") String shortName)
            throws CCAException {
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
    @Operation(
        summary = "Soft delete CCA template",
        description = "Marks a CCA template as deleted"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Deleted template",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CCATemplate.class))),
        @ApiResponse(responseCode = "404", description = "Could not delete the CCA Template")
    })
    public Response removeCCATemplate(
            @Context HttpServletRequest request,
            @Parameter(description = "Template short name") @PathParam("shortName") String shortName)
            throws CCAException {
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
    @Operation(
        summary = "Hard delete CCA template",
        description = "Permanently deletes a CCA template"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Deleted template",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CCATemplate.class))),
        @ApiResponse(responseCode = "404", description = "Could not delete the CCA Template")
    })
    public Response deepRemoveCCATemplate(
            @Context HttpServletRequest request,
            @Parameter(description = "Template short name") @PathParam("shortName") String shortName)
            throws CCAException {
        try {
            AuthorizationUtil.handleAuthorization(request, Arrays.asList(Permissions.ROLE_ADMIN), null);
            return Response.status(Status.OK).entity(ccaContextService.deepRemove(request, shortName)).build();
        } catch (Exception e) {
            throw new CCAException(e);
        }
    }

    @POST
    @Path(ApiConstants.COMMENT)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ValidateUser
    @Operation(
        summary = "Add a comment",
        description = "Adds a comment and returns the logged activity"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Activity",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Activity.class))),
        @ApiResponse(responseCode = "400", description = "Unable to log a comment"),
        @ApiResponse(responseCode = "406", description = "Blank Comment Not allowed")
    })
    public Response addComment(
            @Context HttpServletRequest request,
            @Parameter(name = "commentData", description = "Comment payload") CommentLoggingData commentData,
            @Parameter(description = "Template short name") @PathParam("shortName") String shortName) {
        try {
            CommonProfile profile = AuthUtil.getProfileFromRequest(request);
            Long userId = Long.parseLong(profile.getId());
            if (commentData.getBody().trim().length() > 0) {
                return Response.status(Status.OK).entity(ccaContextService.addComment(request, userId, commentData))
                        .build();
            }
            return Response.status(Status.NOT_ACCEPTABLE).entity("Blank Comment Not allowed").build();
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path(ApiConstants.fieldIds)
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Find CCA field IDs",
        description = "Returns IDs of filterable CCA fields"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of field IDs",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                array = @ArraySchema(schema = @Schema(implementation = Long.class)))),
        @ApiResponse(responseCode = "404", description = "CCA field not found")
    })
    public Response getFields(
            @Context HttpServletRequest request,
            @Parameter(description = "Template short name") @QueryParam("shortName") String shortName,
            @Parameter(description = "Language code") @QueryParam("language") String language) throws CCAException {
        try {
            return Response.status(Status.OK).entity(ccaContextService.getFieldIds(shortName, language)).build();
        } catch (Exception e) {
            throw new CCAException(e);
        }
    }
}
