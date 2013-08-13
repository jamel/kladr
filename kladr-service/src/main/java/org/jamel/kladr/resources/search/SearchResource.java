package org.jamel.kladr.resources.search;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jamel.kladr.AddressObject;
import org.jamel.kladr.KladrSearcher;

/**
 * @author Sergey Polovko
 */
@Path("/search")
@Produces(MediaType.APPLICATION_JSON)
public class SearchResource {

    public static final int MAX_RESULTS_COUNT = 10;

    private final KladrSearcher kladrSearcher;


    public SearchResource(KladrSearcher kladrSearcher) {
        this.kladrSearcher = kladrSearcher;
    }

    @GET
    @Path("/{token}")
    public List<AddressObject> get(@PathParam("token") String token) {
        return kladrSearcher.search(token, MAX_RESULTS_COUNT);
    }
}
