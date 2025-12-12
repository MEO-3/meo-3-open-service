package org.thingai.meo;

import org.thingai.base.Service;
import org.thingai.base.dao.Dao;
import org.thingai.base.dao.DaoFile;
import org.thingai.base.dao.DaoSqlite;
import org.thingai.base.log.ILog;
import org.thingai.meo.callback.MDeviceDiscoverCallback;
import org.thingai.meo.entity.MDevice;
import org.thingai.meo.handler.MDevDiscoverHandler;
import org.thingai.meo.handler.MDevFeatureHandler;
import org.thingai.meo.handler.MDevMgmtHandler;
import org.thingai.meo.handler.MServiceHandler;

public class MeoService extends Service {
    private static final MeoService instance = new MeoService();

    private static MServiceHandler serviceHandler;
    private static MDevMgmtHandler deviceManager;
    private static MDevDiscoverHandler discoverHandler;
    private static MDevFeatureHandler featureHandler;

    // Discovery service thread
    private Thread discoveryThread;

    private MeoService() {

    }

    public static MeoService getInstance() {
        return instance;
    }

    @Override
    protected void onServiceInit() {
        Dao daoSqlite = new DaoSqlite(appDir + "/meo.db");
        DaoFile daoFile = new DaoFile(appDir + "/data");

        ILog.d("MeoService", "SQLite DAO initialized at: " + appDir + "/meo.db");
        ILog.d("MeoService", "DAO File initialized at: " + appDir + "/data");

        daoSqlite.initDao(new Class[]{
            MDevice.class
        });

        deviceManager = new MDevMgmtHandler(daoSqlite);
        serviceHandler = new MServiceHandler();
        discoverHandler = new MDevDiscoverHandler(10);
        featureHandler = new MDevFeatureHandler();

        MDeviceDiscoverCallback discoverCallback = new MDeviceDiscoverCallback() {
            @Override
            public void onDeviceRegistered(MDevice device, String message) {
                ILog.i("MeoService", "Device discovered/registered: " + message);
            }

            @Override
            public void onRegisteredFailed(int errorCode, String errorMessage) {
                ILog.w("MeoService", "Device discovery failed (" + errorCode + "): " + errorMessage);
            }
        };

        MeoDiscoveryService discoveryService = new MeoDiscoveryService(8901, discoverHandler, discoverCallback);
        discoveryThread = new Thread(discoveryService);
        discoveryThread.setDaemon(true);
        discoveryThread.start();
    }
}
