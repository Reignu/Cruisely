package com.cruisely.security;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.PRECONDITION_FAILED;
import static com.cruisely.common.I18n.ETAG_EMPTY_ERROR;
import static com.cruisely.common.I18n.ETAG_INVALID_ERROR;

/**
 * Class responsible for filtering ETag-related request headers
 */
@Provider
@ETagFilterBinding
public class ETagFilter implements ContainerRequestFilter {
    /**
     * Function that checks the validity of the retrieved header
     *
     * @param requestContext context from which the header is retrieved
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String header = requestContext.getHeaderString("If-Match");
        if (header == null || header.isEmpty()) {
            requestContext.abortWith(Response.status(BAD_REQUEST).entity(ETAG_EMPTY_ERROR).build());
        } else if (!EntityIdentitySignerVerifier.validateEntitySignature(header)) {
            requestContext.abortWith(Response.status(PRECONDITION_FAILED).entity(ETAG_INVALID_ERROR).build());
        }
    }
}
