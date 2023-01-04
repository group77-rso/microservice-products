package com.example.products.api.v1.resources;

import com.example.products.lib.Hello;
import com.example.products.services.config.MicroserviceLocations;
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
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;


@Log
@ApplicationScoped
@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductsResource {

    private Logger log = Logger.getLogger(ProductsResource.class.getName());

    @Inject
    private ProductBean productBean;

    @Inject
    private MicroserviceLocations microserviceLocations;
    private static HttpURLConnection conn;

    @Context
    protected UriInfo uriInfo;

    @Operation(description = "Get all products.", summary = "Get all products")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of products",
                    content = @Content(schema = @Schema(implementation = Product.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    @GET
    public Response getProduct() {
        List<Product> products = productBean.getProductsFilter(uriInfo);
        log.log(Level.INFO, String.format("Listing %d products", products.size()));
        return Response.status(Response.Status.OK).entity(products).build();
    }

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
    @Path("/{productId}")
    public Response getProduct(@Parameter(description = "Product ID.", required = true)
                               @PathParam("productId") Integer productId) {

        Product product = productBean.getProducts(productId);

        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        log.info(String.format("Fetching product for id %d", productId));
        return Response.status(Response.Status.OK).entity(product).build();
    }

    @Operation(description = "Add product.", summary = "Add product")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "Product successfully added."
            ),
            @APIResponse(responseCode = "405", description = "Validation error .")
    })
    @POST
    public Response createProduct(@RequestBody(
            description = "DTO object with product.",
            required = true, content = @Content(
            schema = @Schema(implementation = Product.class))) Product product) {

        if (product.getName() == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        else
        {
            product = productBean.createProduct(product);
        }

        return Response.status(Response.Status.OK).entity(product).build();

    }


    @Operation(description = "Update metadata for a product.", summary = "Update product")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Product successfully updated."
            )
    })
    @PUT
    @Path("{productId}")
    public Response putProduct(
            @Parameter(description = "Product ID.", required = true) @PathParam("productId") Integer productId,
            @RequestBody(
                    description = "DTO object with product.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Product.class)
                    )
            ) Product product) {
        product = productBean.putProduct(productId, product);

        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        log.info(String.format("Updating product with id %d", productId));
        return Response.status(Response.Status.NOT_MODIFIED).build();
    }

    @Operation(description = "Delete metadata for a product.", summary = "Delete product")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Product successfully deleted."
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Not found."
            )
    })
    @DELETE
    @Path("{productId}")
    public Response deleteProduct(@Parameter(description = "Product ID.", required = true)
                                  @PathParam("productId") Integer productId) {

        boolean deleted = productBean.deleteProduct(productId);

        if (deleted) {
            log.info(String.format("Product with id %d was deleted.", productId));
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/slow")
    public Response slowHello() throws InterruptedException {

        TimeUnit.SECONDS.sleep(5);

        Hello hello = new Hello();
        hello.setMessege("Hello!");

        return Response.status(Response.Status.OK).entity(hello).build();
    }


}
