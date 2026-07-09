package org.thingai.app.meo;


import org.thingai.app.meo.blemqtt.BlemqttClient;
import org.thingai.app.meo.blemqtt.BlemqttConfig;
import org.thingai.app.meo.entity.MeoDevice;
import org.thingai.app.meo.entity.MeoDeviceCapability;
import org.thingai.app.meo.handler.MeoDeviceHandler;
import org.thingai.app.meo.handler.MeoProvisionHandler;
import org.thingai.base.Service;
import org.thingai.base.dao.Dao;
import org.thingai.base.log.ILog;
import org.thingai.platform.dao.DaoSqlite;

import java.io.File;

public class MeoService extends Service {
    private static final String TAG = "MeoService";

    private Dao dao;
    private BlemqttClient blemqttClient;
    private MeoDeviceHandler deviceHandler;
    private MeoProvisionHandler provisionHandler;

    protected MeoService() {
        super("MeoService");
        setAppDirName("meo_service");
        setVersion("0.1");

        ILog.ENABLE_LOGGING = true;
        ILog.LOG_LEVEL = ILog.DEBUG;
    }

    @Override
    protected void onServiceInit() {
        String dataDir = System.getenv("MEO_DATA_DIR");
        String appDir = dataDir != null && !dataDir.trim().isEmpty() ? dataDir : getAppDir();
        new File(appDir).mkdirs();

        dao = new DaoSqlite(appDir + "/meo.db");
        dao.initDao(new Class[]{
                MeoDevice.class,
                MeoDeviceCapability.class
        });
        deviceHandler = new MeoDeviceHandler(dao);

        BlemqttConfig blemqttConfig = new BlemqttConfig();
        String broker = System.getenv("MEO_MQTT_BROKER");
        if (broker != null && !broker.trim().isEmpty()) {
            blemqttConfig.setBrokerUrl(broker);
        }
        blemqttClient = new BlemqttClient(blemqttConfig);
        try {
            blemqttClient.connect();
            ILog.d(TAG, "blemqtt connect");
        } catch (Exception e) {
            ILog.e(TAG, "blemqtt connect failed", e);
        }
        // TODO: Error handling blemqtt connect failed here
        provisionHandler = new MeoProvisionHandler(blemqttClient, dao);
    }

    @Override
    protected void onServiceShutdown() {
        if (blemqttClient != null) {
            try {
                blemqttClient.disconnect();
            } catch (Exception e) {
                ILog.w(TAG, "blemqtt disconnect failed", e);
            }
        }
        if (dao instanceof DaoSqlite) {
            ((DaoSqlite) dao).close();
        }
    }

    public MeoDeviceHandler getDeviceHandler() {
        return deviceHandler;
    }

    public MeoProvisionHandler getProvisionHandler() {
        return provisionHandler;
    }
}
