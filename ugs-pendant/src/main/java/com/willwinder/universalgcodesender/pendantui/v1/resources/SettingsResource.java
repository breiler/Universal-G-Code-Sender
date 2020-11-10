package com.willwinder.universalgcodesender.pendantui.v1.resources;

import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.pendantui.v1.model.Settings;
import io.swagger.v3.oas.annotations.Operation;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/v1/settings")
public class SettingsResource {

    @Inject
    private BackendAPI backendAPI;

    @GET
    @Path("getSettings")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets all settings", tags = "Settings")
    public Settings getSettings() {
        com.willwinder.universalgcodesender.utils.Settings settings = backendAPI.getSettings();
        Settings response = new Settings();
        response.setJogFeedRate(settings.getJogFeedRate());
        response.setJogStepSizeXY(settings.getManualModeStepSize());
        response.setJogStepSizeZ(settings.getzJogStepSize());
        response.setPreferredUnits(settings.getPreferredUnits());
        return response;
    }

    @POST
    @Path("setSettings")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Sets all settings", tags = "Settings")
    public void setSettings(Settings settings) throws Exception {
        com.willwinder.universalgcodesender.utils.Settings backendSettings = backendAPI.getSettings();
        backendSettings.setJogFeedRate(settings.getJogFeedRate());
        backendSettings.setManualModeStepSize(settings.getJogStepSizeXY());
        backendSettings.setzJogStepSize(settings.getJogStepSizeZ());
        backendSettings.setPreferredUnits(settings.getPreferredUnits());
        backendAPI.applySettings(backendSettings);
    }
}
