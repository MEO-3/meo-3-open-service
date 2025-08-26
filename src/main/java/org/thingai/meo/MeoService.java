package org.thingai.meo;

import org.thingai.base.Service;
import org.thingai.meo.handlers.MeoHandlerDevice;
import org.thingai.meo.handlers.MeoHandlerFlow;
import org.thingai.meo.handlers.MeoHandlerService;

public class MeoService extends Service {
    private static final MeoHandlerService handlerService = new MeoHandlerService();
    private static final MeoHandlerFlow handlerFlow = new MeoHandlerFlow();
    private static final MeoHandlerDevice handlerDevice = new MeoHandlerDevice();

    @Override
    public void onServiceInit() {

    }

    @Override
    public void run() {

    }
}
