package cl.felipebarriga.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Map;

/**
 * Copyright (c) 2015 Felipe Barriga Richards. See Copyright Notice in LICENSE file.
 */
public class PreferencesUtils {

    private static final String LOG_TAG = "SmartBandLogger";
    private final String CLASS = getClass().getSimpleName();

    private final SharedPreferences mSharedPref;
    private final Context mContext;

    public PreferencesUtils( Context context ) {
        mContext = context;
        mSharedPref = PreferenceManager.getDefaultSharedPreferences( mContext );
        showPreferencesValues();
    }

    private void showPreferencesValues() {
        Log.i( LOG_TAG, CLASS + ": showPreferencesValues: BEGIN ---------------------" );
        Map<String, ?> keys = mSharedPref.getAll();

        for( Map.Entry<String, ?> entry : keys.entrySet() ) {
            Log.d( LOG_TAG, CLASS + ": "
                    + entry.getKey() + ": "
                    + entry.getValue().toString() + " : "
                    + entry.getValue().getClass() );
        }
        Log.i( LOG_TAG, CLASS + ": showPreferencesValues: END ---------------------" );
    }

    public int getIntPreference( String name, int defaultValueRes ) {
        Log.d( LOG_TAG, CLASS + ": getIntPreference: name=" + name + " defaultValueRes=" + defaultValueRes );
        String defaultValueStr = mContext.getResources().getString( defaultValueRes );
        String valueStr = mSharedPref.getString( name, defaultValueStr );
        int value = Integer.parseInt( valueStr );

        if( !mSharedPref.contains( name ) ) {
            Log.w( LOG_TAG, CLASS + ": getIntPreference: preference not found! name=" + name );
        }

        return value;
    }

    public float getFloatPreference( String name, int defaultValueRes ) {
        Log.d( LOG_TAG, CLASS + ": getFloatPreference: name=" + name + " defaultValueRes=" + defaultValueRes );
        String defaultValueStr = mContext.getResources().getString( defaultValueRes );
        String valueStr = mSharedPref.getString( name, defaultValueStr );
        float value = Float.parseFloat( valueStr );

        if( !mSharedPref.contains( name ) ) {
            Log.w( LOG_TAG, CLASS + ": getFloatPreference: preference not found! name=" + name );
        }

        return value;
    }

    public boolean getBooleanPreference( String name, int default_value_res ) {
        Log.d( LOG_TAG, CLASS + ": getBooleanPreference: name=" + name + " default_value_res=" + default_value_res );
        String default_value = mContext.getResources().getString( default_value_res );

        boolean value;

        if( !mSharedPref.contains( name ) ) {
            Log.w( LOG_TAG, CLASS + ": getBooleanPreference: preference not found! name=" + name );
        }

        try {
            value = mSharedPref.getBoolean( name, Boolean.parseBoolean( default_value ) );
        } catch( java.lang.ClassCastException e ) {
            // don't panic, try string
            String valueStr = mSharedPref.getString( name, default_value );
            value = Boolean.parseBoolean( valueStr );
        }

        return value;
    }

    public String getStrPreference( String name, int default_value ) {
        String value = mSharedPref
                .getString(
                        name,
                        mContext.getResources().getString( default_value )
                );

        if( !mSharedPref.contains( name ) ) {
            Log.w( LOG_TAG, CLASS + ": getStrPreference: preference not found! name=" + name );
        }

        return value;
    }
}
