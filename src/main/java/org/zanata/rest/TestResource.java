package org.zanata.rest;

import com.google.inject.Inject;
import org.zanata.model.HPerson;
import org.zanata.security.annotations.Authenticated;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Patrick Huang <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@Path("/test")
@Produces
@Consumes
public class TestResource {

    @Inject @Authenticated
    HPerson person;

    @GET
    Response getPerson() {
        return Response.ok(person.getName(), MediaType.TEXT_PLAIN_TYPE).build();
    }
}
