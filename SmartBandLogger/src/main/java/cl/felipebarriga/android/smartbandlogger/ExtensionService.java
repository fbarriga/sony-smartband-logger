package cl.felipebarriga.android.smartbandlogger;

import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;

import android.util.Log;

/**
 * Copyright (c) 2015 Felipe Barriga Richards. See Copyright Notice in LICENSE file.
 */
public class ExtensionService extends com.sonyericsson.extras.liveware.extension.util.ExtensionService {

    public static final String EXTENSION_KEY = "cl.felipebarriga.android.smartbandlogger.key";

    private static final String LOG_TAG = "SmartBandLogger";
    private final String CLASS = getClass().getSimpleName();

    public ExtensionService() {
        super( EXTENSION_KEY );
    }

    /**
     * {@inheritDoc}
     *
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d( LOG_TAG, CLASS + ": onCreate" );
    }

    @Override
    protected com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation getRegistrationInformation() {
        return new RegistrationInformation( this );
    }

    /* (non-Javadoc)
     * @see com.sonyericsson.extras.liveware.aef.util.ExtensionService#keepRunningWhenConnected()
     */
    @Override
    protected boolean keepRunningWhenConnected() {
        return true;
    }

    @Override
    public ControlExtension createControlExtension( String hostAppPackageName ) {
        return new LoggerControl( hostAppPackageName, this );
    }
}
