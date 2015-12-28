package cl.felipebarriga.android.smartbandlogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Copyright (c) 2015 Felipe Barriga Richards. See Copyright Notice in LICENSE file.
 */

/**
 * To make this work you need to convert this app into a system app.

 Intent i = new Intent( BatteryInfoReceiver.Battery.ACTION_QUERY_BATTERY_STATUS );
 i.setComponent( ComponentName.unflattenFromString( BatteryInfoReceiver.Battery.QUERY_RECEIVER ) );
 try {
    mContext.sendBroadcast( i, Registration.HOSTAPP_PERMISSION );
 } catch( Throwable e ) {
    e.printStackTrace();
 }

 */
public class BatteryInfoReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "SmartBandLogger";
    private final String CLASS = getClass().getSimpleName();

    public static class Battery {
        public static final String QUERY_RECEIVER = "com.sonymobile.smartconnect.hostapp.ellis/.receivers.BatteryReceiver";
        public static final String ACTION_QUERY_BATTERY_STATUS = "com.sonymobile.smartconnect.QUERY_BATTERY_STATUS";
        public static final String ACTION_BATTERY_STATUS = "com.sonymobile.smartconnect.DEVICE_BATTERY_STATUS";
        public static final String EXTRA_BATTERY_LEVEL = "Level";
        public static final String EXTRA_BATTERY_TIME_STAMP = "Timestamp";
        public static final String EXTRA_SC_ACC_ADDRESS = "Address";
        public static final String EXTRA_MAX_BATTERY_LEVEL = "Max";
    }

    public class BatteryInfo {
        final public long mTimestamp;
        final public int mLevel;
        final public int mMaxLevel;
        final public String mDeviceAddress;

        public BatteryInfo( long timestamp, int level, int maxLevel, String deviceAddress ) {
            this.mTimestamp = timestamp;
            this.mLevel = level;
            this.mMaxLevel = maxLevel;
            this.mDeviceAddress = deviceAddress;
        }

        public float getLevelPercentage() {
            return ( mLevel / mMaxLevel ) * 100.0f;
        }

        @Override
        public String toString() {
            return "ts=" + mTimestamp + " level=" + mLevel + " maxLevel=" + mMaxLevel + " address=" + mDeviceAddress;
        }
    }

    public void onReceive( Context context, Intent intent ) {
        if( intent != null && intent.getAction() != null ) {
            String action = intent.getAction();

            if( Battery.ACTION_BATTERY_STATUS.equals( action ) ) {
                String deviceAddress = intent.getStringExtra( Battery.EXTRA_SC_ACC_ADDRESS );

                long timestamp = intent.getLongExtra( Battery.EXTRA_BATTERY_TIME_STAMP, 0 );
                int level = intent.getIntExtra( Battery.EXTRA_BATTERY_LEVEL, 0 );
                int maxLevel = intent.getIntExtra( Battery.EXTRA_MAX_BATTERY_LEVEL, 100 );
                BatteryInfo batteryInfo = new BatteryInfo( timestamp, level, maxLevel, deviceAddress );
                Log.i( LOG_TAG, CLASS + ": onReceive: action=" + action + " batteryInfo=" + batteryInfo.toString() );
            } else {
                Log.w( LOG_TAG, CLASS + ": onReceive: unknown action! action=" + action );
            }
        }
    }
}
