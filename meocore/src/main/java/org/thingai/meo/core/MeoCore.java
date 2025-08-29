package org.thingai.meo.core;

import org.thingai.meo.core.handlers.MeoHandlerDevice;
import org.thingai.meo.core.handlers.MeoHandlerFlow;
import org.thingai.meo.core.handlers.MeoHandlerService;

public class MeoCore {
    private static final MeoHandlerService handlerService = new MeoHandlerService();
    private static final MeoHandlerFlow handlerFlow = new MeoHandlerFlow();
    private static final MeoHandlerDevice handlerDevice = new MeoHandlerDevice();

    public static MeoHandlerService serviceHandler() {
        return handlerService;
    }

    public static MeoHandlerFlow flowHandler() {
        return handlerFlow;
    }

    public static MeoHandlerDevice deviceHandler() {
        return handlerDevice;
    }
}
