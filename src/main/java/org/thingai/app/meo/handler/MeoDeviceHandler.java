package org.thingai.app.meo.handler;

import org.thingai.app.meo.define.PermissionType;
import org.thingai.app.meo.entity.MeoDevice;
import org.thingai.app.meo.entity.MeoDeviceCapability;
import org.thingai.app.meo.entity.MeoDeviceProfile;
import org.thingai.base.dao.Dao;

import java.util.HashSet;
import java.util.Set;

public class MeoDeviceHandler {
    private static final String TAG = "MeoDeviceHandler";
    private static final int VALID_CAPABILITY_PERMISSION =
            PermissionType.EXECUTE | PermissionType.WRITE | PermissionType.READ;

    private final Dao dao;

    public MeoDeviceHandler(Dao dao) {
        this.dao = dao;
    }

    public MeoDeviceProfile[] listDeviceProfiles() {
        MeoDeviceProfile[] profiles = dao.readAll(MeoDeviceProfile.class);
        if (profiles == null) {
            return new MeoDeviceProfile[0];
        }

        for (MeoDeviceProfile profile : profiles) {
            attachCapabilities(profile);
        }
        return profiles;
    }

    public MeoDeviceProfile getDeviceProfile(String profileId) {
        if (isBlank(profileId)) {
            return null;
        }

        MeoDeviceProfile[] profiles = dao.query(MeoDeviceProfile.class, "profileId", profileId);
        if (profiles == null || profiles.length == 0) {
            return null;
        }

        MeoDeviceProfile profile = profiles[0];
        attachCapabilities(profile);
        return profile;
    }

    public MeoDeviceProfile saveDeviceProfile(MeoDeviceProfile profile) {
        validateProfile(profile);

        MeoDeviceCapability[] capabilities = profile.getCapabilities();
        profile.setCapabilities(null);
        dao.insertOrUpdate(profile);
        profile.setCapabilities(capabilities);

        dao.deleteByColumn(MeoDeviceCapability.class, "profileId", profile.getProfileId());
        if (capabilities != null && capabilities.length > 0) {
            for (MeoDeviceCapability capability : capabilities) {
                capability.setProfileId(profile.getProfileId());
                capability.setCapabilityKey(capabilityKey(profile.getProfileId(), capability.getCapabilityId()));
            }
            dao.insertBatch(capabilities);
        }

        return getDeviceProfile(profile.getProfileId());
    }

    public boolean deleteDeviceProfile(String profileId) {
        MeoDeviceProfile profile = getDeviceProfile(profileId);
        if (profile == null) {
            return false;
        }

        dao.deleteByColumn(MeoDeviceCapability.class, "profileId", profileId);
        dao.delete(MeoDeviceProfile.class, profileId);
        return true;
    }

    public MeoDevice getDevice(String deviceId) {
        return null;
    }

    private void attachCapabilities(MeoDeviceProfile profile) {
        if (profile == null || isBlank(profile.getProfileId())) {
            return;
        }

        MeoDeviceCapability[] capabilities = dao.query(MeoDeviceCapability.class, "profileId", profile.getProfileId());
        profile.setCapabilities(capabilities != null ? capabilities : new MeoDeviceCapability[0]);
    }

    private void validateProfile(MeoDeviceProfile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("device profile is required");
        }
        if (isBlank(profile.getProfileId())) {
            throw new IllegalArgumentException("profileId is required");
        }
        if (isBlank(profile.getName())) {
            throw new IllegalArgumentException("name is required");
        }
        if (profile.getVersion() <= 0) {
            throw new IllegalArgumentException("version must be greater than 0");
        }

        validateCapabilities(profile.getCapabilities());
    }

    private void validateCapabilities(MeoDeviceCapability[] capabilities) {
        if (capabilities == null) {
            return;
        }

        Set<Integer> capabilityIds = new HashSet<>();
        for (MeoDeviceCapability capability : capabilities) {
            validateCapability(capability, capabilityIds);
        }
    }

    private void validateCapability(MeoDeviceCapability capability, Set<Integer> capabilityIds) {
        if (capability == null) {
            throw new IllegalArgumentException("capability is required");
        }
        if (capability.getCapabilityId() <= 0) {
            throw new IllegalArgumentException("capabilityId must be greater than 0");
        }
        if (!capabilityIds.add(capability.getCapabilityId())) {
            throw new IllegalArgumentException("capabilityId must be unique inside a profile");
        }
        if (isBlank(capability.getName())) {
            throw new IllegalArgumentException("capability name is required");
        }
        if ((capability.getPermission() & ~VALID_CAPABILITY_PERMISSION) != 0) {
            throw new IllegalArgumentException("capability permission contains unsupported bits");
        }
    }

    private String capabilityKey(String profileId, int capabilityId) {
        return profileId + ":" + capabilityId;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
