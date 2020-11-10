package com.willwinder.universalgcodesender.pendantui;

import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.pendantui.v1.resources.*;
import com.willwinder.universalgcodesender.services.JogService;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api")
public class AppConfig extends ResourceConfig {
    public AppConfig() {
        register(OpenApiResource.class);
        register(ExceptionMapper.class);
        register(StatusResource.class);
        register(MachineResource.class);
        register(TextResource.class);
        register(MacrosResource.class);
        register(SettingsResource.class);
        register(FilesResource.class);
        register(MultiPartFeature.class);

        BackendAPI backendAPI = BackendAPIFactory.getInstance().getBackendAPI();
        JogService jogService = new JogService(backendAPI);

        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(backendAPI).to(BackendAPI.class);
                bind(jogService).to(JogService.class);
            }
        });
    }
}