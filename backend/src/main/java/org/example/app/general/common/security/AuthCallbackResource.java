package org.example.app.general.common.security;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.*;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.util.HashMap;
import java.util.Map;

@Path("/auth/callback")
public class AuthCallbackResource {

    @Inject
    SessionService sessionService;

    @Inject
    JwtService jwtService;

    @Inject
    ContainerRequestContext requestContext;

    @GET
    public Response callback(@QueryParam("code") String code,
                             @Context HttpHeaders headers) {
        if (code == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Authorization code missing").build();
        }

        // Step 2: Get the session ID from the cookie
        Cookie sessionCookie = headers.getCookies().get("SESSION_ID");
        if (sessionCookie == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing session ID cookie").build();
        }
        String sessionId = sessionCookie.getValue();
        String originalUrl = sessionService.getSession(sessionId).get().getOriginalUrl();

        String jwt = jwtService.exchangeCodeForToken(code);
        // Step 3: Store the code in Redis under the session ID
        sessionService.storeSession(sessionId, jwt, "");

        // Step 4: Redirect to the original URL the user wanted to access
        if (originalUrl != null && !originalUrl.isEmpty()) {
            return Response.status(Response.Status.FOUND)
                    .header("Location", originalUrl)  // Redirect to the original URL
                    .build();
        } else {
            return Response.ok("Authenticated").build();
        }
    }
}
