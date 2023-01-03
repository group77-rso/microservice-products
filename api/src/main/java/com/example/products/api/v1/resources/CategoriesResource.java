package com.example.products.api.v1.resources;

import com.example.products.lib.Category;
import com.example.products.lib.Product;
import com.example.products.services.beans.CategoryBean;
import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

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
public class CategoriesResource {

    private Logger log = Logger.getLogger(CategoriesResource.class.getName());

    @Inject
    private CategoryBean categoryBean;

    @Context
    protected UriInfo uriInfo;


    @Operation(description = "Get all categories.", summary = "Get all categories")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of categories",
                    content = @Content(schema = @Schema(implementation = Category.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    @GET
    public Response getCategory() {
        List<Category> categories = categoryBean.getCategoriesFilter(uriInfo);
        log.log(Level.INFO, String.format("Listing %d categories", categories.size()));
        return Response.status(Response.Status.OK).entity(categories).build();
    }

    @Operation(description = "Get data for a category.", summary = "Get data for a category")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Category",
                    content = @Content(
                            schema = @Schema(implementation = Product.class))
            )})
    @GET
    @Path("/{categoryId}")
    public Response getCategory(@Parameter(description = "Category ID.", required = true) @PathParam("categoryId") Integer categoryId) {
        Category category = categoryBean.getCategories(categoryId);
        if (category == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        log.info(String.format("Fetching category for id %d", categoryId));
        return Response.status(Response.Status.OK).entity(category).build();
    }

    @Operation(description = "Add category.", summary = "Add category")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "Category successfully added."
            ),
            @APIResponse(responseCode = "405", description = "Validation error .")
    })
    @POST
    public Response careateCategory(@RequestBody(
            description = "DTO object with product.",
            required = true, content = @Content(
            schema = @Schema(implementation = Category.class))) Category category) {

        if (category.getName() == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        else
        {
            category = categoryBean.createCategory(category);
        }

        return Response.status(Response.Status.OK).entity(category).build();

    }


    @Operation(description = "Update information about a category.", summary = "Update category")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Category successfully updated."
            )
    })
    @PUT
    @Path("{categoryId}")
    public Response putProduct(
            @Parameter(description = "Category ID.", required = true) @PathParam("categoryId") Integer categoryId,
            @RequestBody(
                    description = "DTO object with category.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Category.class)
                    )
            ) Category category) {
        category = categoryBean.putCategory(categoryId, category);

        if (category == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        log.info(String.format("Updating category with id %d", categoryId));
        return Response.status(Response.Status.NOT_MODIFIED).build();
    }

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
    @DELETE
    @Path("{categoryId}")
    public Response deleteProduct(@Parameter(description = "Category ID.", required = true)
                                  @PathParam("categoryId") Integer categoryId) {

        boolean deleted = categoryBean.deleteCategory(categoryId);

        if (deleted) {
            log.info(String.format("Category with id %d deleted.", categoryId));
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }


}
