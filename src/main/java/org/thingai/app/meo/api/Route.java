package org.thingai.app.meo.api;

import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import org.thingai.app.meo.entity.MeoDeviceProfile;
import org.thingai.app.meo.handler.MeoDeviceHandler;

import java.util.HashMap;
import java.util.Map;

public class Route {
    private final JavalinConfig config;
    private final MeoDeviceHandler deviceHandler;

    public Route(JavalinConfig config, MeoDeviceHandler deviceHandler) {
        this.config = config;
        this.deviceHandler = deviceHandler;
    }

    public void addRoutes() {
        config.routes.get("/", ctx -> {
            ctx.json("meow");
        });

        config.routes.get("/api/v1/device-profiles", this::listDeviceProfiles);
        config.routes.get("/api/v1/device-profiles/{profileId}", this::getDeviceProfile);
        config.routes.post("/api/v1/device-profiles", this::createDeviceProfile);
        config.routes.put("/api/v1/device-profiles/{profileId}", this::updateDeviceProfile);
        config.routes.delete("/api/v1/device-profiles/{profileId}", this::deleteDeviceProfile);
    }

    private void listDeviceProfiles(Context ctx) {
        ctx.json(deviceHandler.listDeviceProfiles());
    }

    private void getDeviceProfile(Context ctx) {
        MeoDeviceProfile profile = deviceHandler.getDeviceProfile(ctx.pathParam("profileId"));
        if (profile == null) {
            notFound(ctx, "device profile not found");
            return;
        }
        ctx.json(profile);
    }

    private void createDeviceProfile(Context ctx) {
        saveDeviceProfile(ctx, null);
    }

    private void updateDeviceProfile(Context ctx) {
        saveDeviceProfile(ctx, ctx.pathParam("profileId"));
    }

    private void saveDeviceProfile(Context ctx, String pathProfileId) {
        try {
            MeoDeviceProfile profile = ctx.bodyAsClass(MeoDeviceProfile.class);
            if (pathProfileId != null) {
                if (profile.getProfileId() != null && !pathProfileId.equals(profile.getProfileId())) {
                    badRequest(ctx, "profileId in path and body must match");
                    return;
                }
                profile.setProfileId(pathProfileId);
            }
            ctx.json(deviceHandler.saveDeviceProfile(profile));
        } catch (IllegalArgumentException e) {
            badRequest(ctx, e.getMessage());
        }
    }

    private void deleteDeviceProfile(Context ctx) {
        boolean deleted = deviceHandler.deleteDeviceProfile(ctx.pathParam("profileId"));
        if (!deleted) {
            notFound(ctx, "device profile not found");
            return;
        }
        ctx.status(204);
    }

    private void badRequest(Context ctx, String message) {
        ctx.status(400).json(errorBody(message));
    }

    private void notFound(Context ctx, String message) {
        ctx.status(404).json(errorBody(message));
    }

    private Map<String, String> errorBody(String message) {
        Map<String, String> body = new HashMap<>();
        body.put("error", message);
        return body;
    }
}
