package org.thingai.meo;

import org.thingai.base.Service;
import org.thingai.base.dao.Dao;
import org.thingai.base.dao.DaoFile;
import org.thingai.base.dao.DaoSqlite;
import org.thingai.meo.handlers.MeoHandlerDevice;
import org.thingai.meo.handlers.MeoHandlerFlow;
import org.thingai.meo.handlers.MeoHandlerService;

public class MeoService extends Service {
    private static final MeoHandlerService handlerService = new MeoHandlerService();
    private static final MeoHandlerFlow handlerFlow = new MeoHandlerFlow();
    private static final MeoHandlerDevice handlerDevice = new MeoHandlerDevice();

    @Override
    public void onServiceInit() {
        Dao daoSqlite = new DaoSqlite(appDir + "/êmo.db");
        Dao daoFile = new DaoFile(appDir + "/data");

        daoSqlite.initDao(new Class[]{
            // Define your entity classes here
        });

        daoFile.initDao(new Class[]{
            // Define your entity classes here
        });

    }

    @Override
    public void run() {

    }
}
