package com.example.products.api.v1.resources;

import com.example.products.lib.Category;
import com.example.products.lib.Product;
import com.example.products.services.beans.CategoryBean;
import com.kumuluz.ee.cors.annotations.CrossOrigin;
import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


@Log
@ApplicationScoped
@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@CrossOrigin
public class CategoriesResource {

    private Logger log = Logger.getLogger(CategoriesResource.class.getName());

    @Inject
    private CategoryBean categoryBean;

    @Context
    protected UriInfo uriInfo;


    @GET
    @Operation(description = "Get all categories.", summary = "Get all categories")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of categories",
                    content = @Content(schema = @Schema(implementation = Category.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    public Response getCategory(@Parameter(hidden = true) @HeaderParam("requestId") String requestId) {
        requestId = requestId != null ? requestId : UUID.randomUUID().toString();

        List<Category> categories = categoryBean.getCategoriesFilter(uriInfo);
        log.log(Level.INFO, String.format("Listing %d categories", categories.size()) + " - requestId: " + requestId);
        return Response.status(Response.Status.OK).header("requestId", requestId).entity(categories).build();
    }

    @GET
    @Operation(description = "Get data for a category for id.", summary = "Get category")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Category",
                    content = @Content(
                            schema = @Schema(implementation = Product.class))
            )})
    @Path("/{categoryId}")
    public Response getCategory(@Parameter(hidden = true) @HeaderParam("requestId") String requestId,
                                @Parameter(description = "Category ID.", required = true, example = "1001") @PathParam("categoryId") Integer categoryId) {
        requestId = requestId != null ? requestId : UUID.randomUUID().toString();

        Category category = categoryBean.getCategories(categoryId);
        if (category == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        log.info(String.format("Fetching category for id %d", categoryId) + " - requestId: " + requestId);
        return Response.status(Response.Status.OK).header("requestId", requestId).entity(category).build();
    }

    @POST
    @Operation(description = "Add a new category.", summary = "Add category")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "Category successfully added."
            ),
            @APIResponse(responseCode = "405", description = "Validation error .")
    })
    @RequestBody(description = "Details of the category to be created", required = true,
            content = @Content(
                    schema = @Schema(implementation = Category.class),
                    examples = @ExampleObject(name = "Creating category", value = "{\n" +
                            "    \"name\": \"Sladoled\"\n" +
                            "}")))
    public Response createCategory(@Parameter(hidden = true) @HeaderParam("requestId") String requestId,
                                   @RequestBody(
            description = "DTO object with category.",
            required = true, content = @Content(
            schema = @Schema(implementation = Category.class))) Category category) {

        requestId = requestId != null ? requestId : UUID.randomUUID().toString();

        if (category.getName() == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        else
        {
            category = categoryBean.createCategory(category);
        }

        log.info(String.format("New category with id %d was created.", category.getCategoryId()) + " - requestId: " + requestId);
        return Response.status(Response.Status.OK).header("requestId", requestId).entity(category).build();

    }

    @PUT
    @Operation(description = "Update information about a category.", summary = "Update category")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Category successfully updated."
            )
    })
    @RequestBody(description = "Details of the Item to be created", required = true,
            content = @Content(
                    schema = @Schema(implementation = Category.class),
                    examples = @ExampleObject(name = "Updating category", value = "{\n" +
                            "    \"name\": \"Updated name\"\n" +
                            "}")))
    @Path("{categoryId}")
    public Response putProduct(@Parameter(hidden = true) @HeaderParam("requestId") String requestId,
            @Parameter(description = "Category ID.", required = true, example = "1001") @PathParam("categoryId") Integer categoryId,
            @RequestBody(
                    description = "DTO object with category.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Category.class)
                    )
            ) Category category) {
        requestId = requestId != null ? requestId : UUID.randomUUID().toString();

        category = categoryBean.putCategory(categoryId, category);

        if (category == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        log.info(String.format("Updating category with id %d", categoryId) + " - requestId: " + requestId);
        return Response.status(Response.Status.OK).header("requestId", requestId).build();
    }

    @DELETE
    @Operation(description = "Delete data for a category.", summary = "Delete category")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Category successfully deleted."
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Not found."
            )
    })
    @Path("{categoryId}")
    public Response deleteProduct(@Parameter(hidden = true) @HeaderParam("requestId") String requestId,
                                  @Parameter(description = "Category ID.", required = true, example = "1001") @PathParam("categoryId") Integer categoryId) {
        requestId = requestId != null ? requestId : UUID.randomUUID().toString();

        boolean deleted = categoryBean.deleteCategory(categoryId);

        if (deleted) {
            log.info(String.format("Category with id %d deleted.", categoryId) + " - requestId: " + requestId);
            return Response.status(Response.Status.NO_CONTENT).header("requestId", requestId).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).header("requestId", requestId).build();
        }
    }


}
