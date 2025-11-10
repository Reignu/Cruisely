package com.cruisely.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.Produces;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

public class CruiseHubConfig extends ResourceConfig {

    public CruiseHubConfig() {
        register(MarshallingFeature.class);
    }

    private static class MarshallingFeature implements Feature {

        @Override
        public boolean configure(FeatureContext context) {
            context.register(AppJsonProvider.class, MessageBodyReader.class, MessageBodyWriter.class);
            return true;
        }
    }

    @Provider
    @Produces(MediaType.APPLICATION_JSON)
    private static class AppJsonProvider extends JacksonJaxbJsonProvider {
        private static final ObjectMapper mapper = new ObjectMapper();

        public AppJsonProvider() {
            super();
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            setMapper(mapper);
        }
    }
}