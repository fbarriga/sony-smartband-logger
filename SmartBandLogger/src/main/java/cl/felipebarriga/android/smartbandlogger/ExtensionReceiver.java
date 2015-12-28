package cl.felipebarriga.android.smartbandlogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Copyright (c) 2015 Felipe Barriga Richards. See Copyright Notice in LICENSE file.
 */
public class ExtensionReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "SmartBandLogger";
    private final String CLASS = getClass().getSimpleName();

    @Override
    public void onReceive( final Context context, final Intent intent ) {
        Log.d( LOG_TAG, CLASS + ": onReceive: " + intent.getAction() );
        intent.setClass( context, ExtensionService.class );
        context.startService( intent );
    }
}
