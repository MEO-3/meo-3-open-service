package org.thingai.app.meo;


import org.thingai.base.Service;
import org.thingai.base.dao.Dao;
import org.thingai.base.log.ILog;
import org.thingai.platform.dao.DaoSqlite;

public class MeoService extends Service {
    private static final String TAG = "MeoService";

    protected MeoService() {
        super("MeoService");
        setAppDirName("meo_service");
        setVersion("0.1");

        ILog.ENABLE_LOGGING = true;
        ILog.LOG_LEVEL = ILog.DEBUG;
    }

    @Override
    protected void onServiceInit() {
        Dao dao = new DaoSqlite(getAppDir()+"/meo.db");

    }
}
