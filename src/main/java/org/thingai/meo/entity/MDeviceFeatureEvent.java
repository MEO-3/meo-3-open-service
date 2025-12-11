package org.thingai.meo.entity;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "device_feature_event")
public class MDeviceFeatureEvent {
    @DaoColumn(name = "device_id", primaryKey = true)
    private String deviceId;

    @DaoColumn(name = "feature_event")
    private String featureEvent;
}
