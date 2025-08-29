package org.thingai.meo;

import org.thingai.base.Service;
import org.thingai.base.dao.Dao;
import org.thingai.base.dao.DaoFile;
import org.thingai.base.dao.DaoSqlite;

public class MeoService extends Service {
    private static final MeoService instance = new MeoService();

    private MeoService() {

    }

    public static MeoService getInstance() {
        return instance;
    }

    @Override
    protected void onServiceInit() {
        Dao daoSqlite = new DaoSqlite(appDir + "/meo.db");
        Dao daoFile = new DaoFile(appDir + "/data");

        daoSqlite.initDao(new Class[]{
            // Define your entity classes here
        });

        daoFile.initDao(new Class[]{
            // Define your entity classes here
        });

    }

    @Override
    protected void onServiceRun() {
        System.out.println("MeoService is running...");
    }
}
