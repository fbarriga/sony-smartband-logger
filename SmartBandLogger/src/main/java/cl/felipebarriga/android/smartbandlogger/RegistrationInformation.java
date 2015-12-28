package cl.felipebarriga.android.smartbandlogger;

import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.aef.sensor.Sensor;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensor;

import android.content.ContentValues;
import android.content.Context;

/**
 * Copyright (c) 2015 Felipe Barriga Richards. See Copyright Notice in LICENSE file.
 */
public class RegistrationInformation extends com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation {

    private final Context mContext;

    private static final String LOG_TAG = "SmartBandLogger";
    private final String CLASS = getClass().getSimpleName();

    /**
     * Create sensor registration object
     *
     * @param context The context
     */
    protected RegistrationInformation( Context context ) {
        if( context == null ) {
            throw new IllegalArgumentException( "context == null" );
        }
        mContext = context;
    }

    @Override
    public int getRequiredControlApiVersion() {
        return 1;
    }

    @Override
    public int getRequiredSensorApiVersion() {
        return 1;
    }

    @Override
    public int getRequiredNotificationApiVersion() {
        return com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation.API_NOT_REQUIRED;
    }

    @Override
    public int getRequiredWidgetApiVersion() {
        return com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation.API_NOT_REQUIRED;
    }

    /**
     * Get the extension registration information.
     *
     * @return The registration configuration.
     */
    @Override
    public ContentValues getExtensionRegistrationConfiguration() {
        String iconHostapp = ExtensionUtils.getUriString( mContext, R.drawable.icon );
        String iconExtension = ExtensionUtils.getUriString( mContext, R.drawable.icon );

        ContentValues values = new ContentValues();

        values.put( Registration.ExtensionColumns.CONFIGURATION_ACTIVITY,
                SettingsActivity.class.getName() );
        values.put( Registration.ExtensionColumns.CONFIGURATION_TEXT,
                mContext.getString( R.string.configuration_text ) );
        values.put( Registration.ExtensionColumns.NAME, mContext.getString( R.string.extension_name ) );
        values.put( Registration.ExtensionColumns.EXTENSION_KEY,
                ExtensionService.EXTENSION_KEY );
        values.put( Registration.ExtensionColumns.HOST_APP_ICON_URI, iconHostapp );
        values.put( Registration.ExtensionColumns.EXTENSION_ICON_URI, iconExtension );
        values.put( Registration.ExtensionColumns.NOTIFICATION_API_VERSION,
                getRequiredNotificationApiVersion() );
        values.put( Registration.ExtensionColumns.PACKAGE_NAME, mContext.getPackageName() );

        return values;
    }

    @Override
    public boolean isSensorSupported( AccessorySensor sensor ) {
        return Sensor.SENSOR_TYPE_ACCELEROMETER.equals( sensor.getType().getName() );
    }

}
