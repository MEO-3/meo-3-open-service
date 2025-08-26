package org.thingai.meo;

public class Main {
    public static void main(String[] args) {
        MeoService.name = "MeoService";
        MeoService.appDirName = "meo_service";
        MeoService.version = "1.0.0";

        MeoService meoService = new MeoService();
        meoService.init();
        meoService.run();
    }
}
