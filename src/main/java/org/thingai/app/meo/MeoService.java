package org.thingai.app.meo;

import com.google.gson.Gson;
import org.thingai.base.Service;
import org.thingai.base.dao.Dao;
import org.thingai.platform.dao.DaoFile;
import org.thingai.platform.dao.DaoSqlite;
import org.thingai.base.log.ILog;
import org.thingai.meo.common.entity.MDevice;
import org.thingai.meo.common.entity.MDeviceFeatureEvent;
import org.thingai.meo.common.entity.MDeviceFeatureMethod;
import org.thingai.app.meo.handler.MDevDiscoverHandler;
import org.thingai.app.meo.handler.MDevFeatureHandler;
import org.thingai.app.meo.handler.MDevMgmtHandler;
import org.thingai.app.meo.handler.MServiceHandler;

public class MeoService extends Service {
    private static final MeoService instance = new MeoService();

    private static MServiceHandler serviceHandler;
    private static MDevMgmtHandler deviceManager;
    private static MDevDiscoverHandler discoverHandler;
    private static MDevFeatureHandler featureHandler;
    private static Gson gson = new Gson();

    // Discovery service thread
    private Thread discoveryThread;

    private MeoService() {

    }

    public static MeoService getInstance() {
        return instance;
    }

    @Override
    protected void onServiceInit() {
        Dao dao = new DaoSqlite(appDir + "/meo.db");
        DaoFile daoFile = new DaoFile(appDir + "/data");

        ILog.d("MeoService", "SQLite DAO initialized at: " + appDir + "/meo.db");
        ILog.d("MeoService", "DAO File initialized at: " + appDir + "/data");

        dao.initDao(new Class[]{
            MDevice.class,
            MDeviceFeatureEvent.class,
            MDeviceFeatureMethod.class
        });

        featureHandler = new MDevFeatureHandler(dao);
        deviceManager = new MDevMgmtHandler(dao, featureHandler);
        serviceHandler = new MServiceHandler();
        discoverHandler = new MDevDiscoverHandler(10, deviceManager);

        // Start device discovery service
        MeoDiscoveryService discoveryService = new MeoDiscoveryService(8901, discoverHandler);
        discoveryThread = new Thread(discoveryService);
        discoveryThread.setDaemon(true);
        discoveryThread.start();
    }

    public static MServiceHandler serviceHandler() {
        return serviceHandler;
    }

    public static MDevMgmtHandler deviceManager() {
        return deviceManager;
    }

    public static MDevDiscoverHandler discoverHandler() {
        return discoverHandler;
    }

    public static MDevFeatureHandler featureHandler() {
        return featureHandler;
    }

    public static Gson getGson() {
        return gson;
    }
}
