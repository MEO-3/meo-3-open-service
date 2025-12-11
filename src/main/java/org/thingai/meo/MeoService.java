package org.thingai.meo;

import org.thingai.base.Service;
import org.thingai.base.dao.Dao;
import org.thingai.base.dao.DaoFile;
import org.thingai.base.dao.DaoSqlite;
import org.thingai.base.log.ILog;
import org.thingai.meo.entity.MDevice;
import org.thingai.meo.handlers.MDeviceHandler;
import org.thingai.meo.handlers.MServiceHandler;

public class MeoService extends Service {
    private static final MeoService instance = new MeoService();

    private static final MServiceHandler flowHandler = new MServiceHandler();
    private static final MDeviceHandler deviceHandler = new MDeviceHandler();

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
    }

    public static MServiceHandler getFlowHandler() {
        return flowHandler;
    }

    public static MDeviceHandler getDeviceHandler() {
        return deviceHandler;
    }
}
