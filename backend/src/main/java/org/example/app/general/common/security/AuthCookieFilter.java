package org.example.app.general.common.security;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.Optional;

@Provider
public class AuthCookieFilter implements ContainerRequestFilter {

    @Inject
    SessionService sessionService;

    @Inject
    JwtService jwtService;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        requestContext.getHeaders().add("Access-Control-Allow-Origin", "http://localhost:3000"); // Allow frontend to access the backend
        requestContext.getHeaders().add("Access-Control-Allow-Credentials", "true"); // Allow credentials (cookies)
        requestContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS"); // Allow common HTTP methods
        requestContext.getHeaders().add("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization"); // Allow headers commonly used in CORS requests


        // Check if the request is for the callback endpoint
        if (requestContext.getUriInfo().getPath().equals("/auth/callback")) {
            return; // Skip filter for the callback
        }

        // Save the original URL (the endpoint the user originally requested)
        String originalUrl = requestContext.getHeaderString("Referer");

        // Store the original URL in the session or as a query parameter
        // For simplicity, we store it as a query parameter
        String authRedirectUrl = jwtService.buildKeycloakAuthUrl();

        // Retrieve session cookie and check if valid
        Cookie sessionCookie = requestContext.getCookies().get("SESSION_ID");

        if (sessionCookie == null || !isValidSession(sessionCookie.getValue())) {
            sessionService.storeSession(sessionCookie.getValue(), "", originalUrl);
            // Redirect to Keycloak if no valid session found
            requestContext.abortWith(
                    Response.status(Response.Status.FOUND)
                            .header("Location", authRedirectUrl)
                            .build()
            );
        }
    }

    private boolean isValidSession(String sessionId) {
        Optional<Session> session = sessionService.getSession(sessionId);
        return sessionService.getSession(sessionId).isPresent() && !session.get().getJwt().isEmpty();
    }
}
