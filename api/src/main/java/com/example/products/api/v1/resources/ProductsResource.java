package com.example.products.api.v1.resources;

import com.example.products.lib.Hello;
import com.example.products.services.config.MicroserviceLocations;
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
import com.example.products.lib.Product;
import com.example.products.services.beans.ProductBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;


@Log
@ApplicationScoped
@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@CrossOrigin
public class ProductsResource {

    private Logger log = Logger.getLogger(ProductsResource.class.getName());

    @Inject
    private ProductBean productBean;

    @Inject
    private MicroserviceLocations microserviceLocations;
    private static HttpURLConnection conn;

    @Context
    protected UriInfo uriInfo;

    @GET
    @Operation(description = "Get all products.", summary = "Get all products")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of products",
                    content = @Content(schema = @Schema(implementation = Product.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    public Response getProduct(@Parameter(hidden = true) @Parameter(hidden = true) @HeaderParam("requestId") String requestId) {
        requestId = requestId != null ? requestId : UUID.randomUUID().toString();

        List<Product> products = productBean.getProductsFilter(uriInfo);
        log.log(Level.INFO, String.format("Listing %d products", products.size()) + " - requestId: " + requestId);
        return Response.status(Response.Status.OK).header("requestId", requestId).entity(products).build();
    }

    // todo: remove/repurpose this method!
    @GET
    @Path("hehe")
    public Response testMerchants() {
        StringBuilder responseContent = new StringBuilder();
        System.out.println("Will connect to " + microserviceLocations.getMerchants() + "/v1/merchants");
        try{
            URL url = new URL(microserviceLocations.getMerchants() + "/v1/merchants");
            conn = (HttpURLConnection) url.openConnection();

            // Request setup
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
            conn.setReadTimeout(5000);

            // Test if the response from the server is successful
            int status = conn.getResponseCode();

            BufferedReader reader;
            String line;
            if (status >= 300) {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            }
            else {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            }
            log.info("response code: " + status);
            System.out.println(responseContent.toString());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            conn.disconnect();
        }

        return Response.status(Response.Status.NOT_MODIFIED).build();
    }

    @GET
    @Operation(description = "Get product by id.", summary = "Get a product ")
    @Path("/{productId}")
    public Response getProduct(@Parameter(hidden = true) @Parameter(hidden = true) @HeaderParam("requestId") String requestId,
                               @Parameter(description = "Product ID.", required = true, example = "1001")
                               @PathParam("productId") Integer productId) {
        requestId = requestId != null ? requestId : UUID.randomUUID().toString();

        Product product = productBean.getProducts(productId);

        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        log.info(String.format("Fetching product for id %d", productId) + " - requestId: " + requestId);
        return Response.status(Response.Status.OK).header("requestId", requestId).entity(product).build();
    }

    @POST
    @Operation(description = "Add product to the database.", summary = "Add a product")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "Product successfully added."),
            @APIResponse(responseCode = "405", description = "Validation error .")
    })
    @RequestBody(description = "Details of the product to be created", required = true,
            content = @Content(
                    schema = @Schema(implementation = Product.class),
                    examples = @ExampleObject(name = "UCreating product", value = "{\n" +
                            "    \"name\": \"Kokosovo mleko\",\n" +
                            "    \"categoryId\": 1001\n" +
                            "}")))
    public Response createProduct(@Parameter(hidden = true) @Parameter(hidden = true) @HeaderParam("requestId") String requestId,
                                  @RequestBody(
            description = "DTO object with product.",
            required = true, content = @Content(
            schema = @Schema(implementation = Product.class))) Product product) {
        requestId = requestId != null ? requestId : UUID.randomUUID().toString();

        if (product.getName() == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        else
        {
            product = productBean.createProduct(product);
        }

        log.info(String.format("New product with id %d was created.", product.getProductId()) + " - requestId: " + requestId);
        return Response.status(Response.Status.OK).header("requestId", requestId).entity(product).build();

    }


    @PUT
    @Operation(description = "Update information for a product.", summary = "Update product")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Product successfully updated.")
    })
    @RequestBody(description = "Details to update", required = true,
            content = @Content(
                    schema = @Schema(implementation = Product.class),
                    examples = @ExampleObject(name = "Updating product", value = "{\n" +
                            "    \"name\": \"Updated name\"\n" +
                            "}")))
    @Path("{productId}")
    public Response putProduct(@Parameter(hidden = true) @Parameter(hidden = true) @HeaderParam("requestId") String requestId,
            @Parameter(description = "Product ID.", required = true, example = "1001") @PathParam("productId") Integer productId,
            @RequestBody(
                    description = "DTO object with product.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Product.class)
                    )
            ) Product product) {
        requestId = requestId != null ? requestId : UUID.randomUUID().toString();

        product = productBean.putProduct(productId, product);

        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        log.info(String.format("Updating product with id %d", productId) + " - requestId: " + requestId);
        return Response.status(Response.Status.OK).header("requestId", requestId).build();
    }

    @DELETE
    @Operation(description = "Delete data for a product.", summary = "Delete product")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Product successfully deleted."
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Not found.")
    })
    @Path("{productId}")
    public Response deleteProduct(@Parameter(hidden = true) @HeaderParam("requestId") String requestId,
                                  @Parameter(description = "Product ID.", required = true) @PathParam("productId") Integer productId) {
        requestId = requestId != null ? requestId : UUID.randomUUID().toString();

        boolean deleted = productBean.deleteProduct(productId);

        if (deleted) {
            log.info(String.format("Product with id %d was deleted.", productId) + " - requestId: " + requestId);
            return Response.status(Response.Status.NO_CONTENT).header("requestId", requestId).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).header("requestId", requestId).build();
        }
    }

    @GET
    @Operation(description = "Waits for 5 second then returns a greeting.", summary = "Dummy method for demonstration purposes")
    @Path("/slow")
    public Response slowHello(@Parameter(hidden = true) @HeaderParam("requestId") String requestId) throws InterruptedException {
        requestId = requestId != null ? requestId : UUID.randomUUID().toString();

        TimeUnit.SECONDS.sleep(5);

        Hello hello = new Hello();
        hello.setMessege("Hello!");

        return Response.status(Response.Status.OK).header("requestId", requestId).entity(hello).build();
    }


}
