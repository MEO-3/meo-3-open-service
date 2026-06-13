package org.thingai.app.meo;


import org.thingai.app.meo.entity.MeoDeviceCapability;
import org.thingai.app.meo.entity.MeoDeviceProfile;
import org.thingai.app.meo.handler.MeoDeviceHandler;
import org.thingai.base.Service;
import org.thingai.base.dao.Dao;
import org.thingai.base.log.ILog;
import org.thingai.platform.dao.DaoSqlite;

import java.io.File;

public class MeoService extends Service {
    private static final String TAG = "MeoService";

    private Dao dao;
    private MeoDeviceHandler deviceHandler;

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
                MeoDeviceProfile.class,
                MeoDeviceCapability.class
        });
        deviceHandler = new MeoDeviceHandler(dao);
    }

    @Override
    protected void onServiceShutdown() {
        if (dao instanceof DaoSqlite) {
            ((DaoSqlite) dao).close();
        }
    }

    public MeoDeviceHandler getDeviceHandler() {
        return deviceHandler;
    }
}
