package org.thingai.meo.core;

import org.thingai.meo.core.handlers.MeoHandlerDevice;
import org.thingai.meo.core.handlers.MeoHandlerFlow;
import org.thingai.meo.core.handlers.MeoHandlerService;
import org.thingai.meocore.handlers.MeoHandlerDeviceImpl;
import org.thingai.meocore.handlers.MeoHandlerFlowImpl;
import org.thingai.meocore.handlers.MeoHandlerServiceImpl;

public class MeoCore {
    private static final MeoHandlerService handlerService = new MeoHandlerServiceImpl();
    private static final MeoHandlerFlow handlerFlow = new MeoHandlerFlowImpl();
    private static final MeoHandlerDevice handlerDevice = new MeoHandlerDeviceImpl();

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
