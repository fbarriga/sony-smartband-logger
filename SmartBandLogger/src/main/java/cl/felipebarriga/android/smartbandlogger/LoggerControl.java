package cl.felipebarriga.android.smartbandlogger;

import com.sonyericsson.extras.liveware.aef.sensor.Sensor;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensor;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensorEvent;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensorEventListener;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensorException;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensorManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import cl.felipebarriga.android.utils.CsvLog;
import cl.felipebarriga.android.utils.OdsLog;
import cl.felipebarriga.android.utils.PreferencesUtils;

/**
 * Copyright (c) 2015 Felipe Barriga Richards. See Copyright Notice in LICENSE file.
 */
class LoggerControl extends ControlExtension {

    private static final String LOG_TAG = "SmartBandLogger";
    private final String CLASS = getClass().getSimpleName();

    private static final int DEFAULT_SENSOR_RATE = Sensor.SensorRates.SENSOR_DELAY_FASTEST;
    private static final int NOTIFICATION_ID = 1;

    private CsvLog mCsvLog = null;
    private AccessorySensor mSensor = null;
    private PreferencesUtils mPrefs = null;
    private boolean mDebugMessages = false;

    private LoggerSingleton mLoggerSingleton = null;

    private int mTimeBetweenIntervals = 0;
    private int mIntervalDuration = 0;
    private int mDataRate = DEFAULT_SENSOR_RATE;

    private final AccessorySensorEventListener mListener = new AccessorySensorEventListener() {

        public void onSensorEvent( AccessorySensorEvent sensorEvent ) {
            float[] values = sensorEvent.getSensorValues();
            int accuracy = sensorEvent.getAccuracy();

            // the timestamp came in microseconds but with milliseconds resolution
            // code that generate it: System.currentTimeMillis() * 1000
            long timestamp = sensorEvent.getTimestamp() / 1000;

            mCsvLog.write( timestamp, values[0], values[1], values[2], accuracy );
            mLoggerSingleton.setRecords( mCsvLog.getRecordsSize() );
            mLoggerSingleton.setLogSize( mCsvLog.getSize() );
            mLoggerSingleton.addChartRecord( timestamp, values[0], values[1], values[2] );
        }
    };

    private void createNotification( String logFilename ) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = ( NotificationManager ) mContext.getSystemService( ns );

        Intent intent = new Intent( mContext, MainActivity.class );

        PendingIntent pIntent = PendingIntent.getActivity( mContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT );

        Notification myNotification = new Notification.Builder( mContext )
                .setSmallIcon( R.drawable.icon )
                .setContentTitle( "Recording..." )
                .setContentText( logFilename )
                .setContentIntent( pIntent )
                .setOngoing( true )
                .setPriority( Notification.PRIORITY_HIGH )
                .build();

        nMgr.notify( NOTIFICATION_ID, myNotification );
    }

    private void destroyNotification() {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = ( NotificationManager ) mContext.getSystemService( ns );
        nMgr.cancel( NOTIFICATION_ID );
    }

    /**
     * Create sample sensor control.
     *
     * @param hostAppPackageName Package name of host application.
     * @param context            The context.
     */
    LoggerControl( final String hostAppPackageName, final Context context ) {
        super( context, hostAppPackageName );

        AccessorySensorManager manager = new AccessorySensorManager( context, hostAppPackageName );
        mSensor = manager.getSensor( Sensor.SENSOR_TYPE_ACCELEROMETER );

        mCsvLog = new CsvLog( context );
        mPrefs = new PreferencesUtils( context );
        mLoggerSingleton = LoggerSingleton.getInstance();
    }

    public void onResume() {
        Log.i( LOG_TAG, CLASS + ": onResume: called." );

        mIntervalDuration = mPrefs.getIntPreference( "intervalDurationSeconds", R.string.settings_default_interval );
        if( mIntervalDuration > 0 ) {
            mIntervalDuration *= 1000;
        }

        mTimeBetweenIntervals = mPrefs.getIntPreference( "timeBetweenIntervalsSeconds", R.string.settings_default_time_between_intervals );
        if( mTimeBetweenIntervals > 0 ) {
            mTimeBetweenIntervals *= 1000;
        }

        mDataRate = mPrefs.getIntPreference( "dataRate", R.string.settings_default_rate );
        Log.d( LOG_TAG, CLASS + ": onResume: mDataRate=" + mDataRate );
        mLoggerSingleton.setSensorRate( mDataRate );

        mDebugMessages = mPrefs.getBooleanPreference( "debugMessages", R.string.settings_default_debug );
        startSensor();


        String filenamePrefix  = mPrefs.getStrPreference( "logPrefix", R.string.settings_default_log_prefix );
        mCsvLog.setDebugMessage( mDebugMessages );
        mCsvLog.createLog( filenamePrefix );


        mLoggerSingleton.setRecords( mCsvLog.getRecordsSize() );
        mLoggerSingleton.setLogSize( mCsvLog.getSize() );
        mLoggerSingleton.setLogFilename( mCsvLog.getFilename() );
        mLoggerSingleton.setStatus( LoggerSingleton.Status.RUNNING );

        createNotification( mCsvLog.getFilename() );
    }

    private void closeStream() {
        destroyNotification();
        mCsvLog.closeStream();
        OdsLog odsLog = new OdsLog( mContext );
        try {
            Log.i( LOG_TAG, CLASS + ": closeStream: generating ods file" );
            String odsFilename = odsLog.createLogFromCSV( mCsvLog.getFilename() );
            Log.i( LOG_TAG, CLASS + ": closeStream: ods filename=" + odsFilename );
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    private void startSensor() {
        Log.i( LOG_TAG, CLASS + ": startSensor: called." );

        if( mSensor == null ) {
            Log.d( LOG_TAG, CLASS + ": startSensor: mSensor == null" );
            return;
        }

        try {
            mSensor.registerFixedRateListener( mListener, mDataRate );
        } catch( AccessorySensorException e ) {
            Log.e( LOG_TAG, CLASS + ": startSensor: Failed to register listener." );
            e.printStackTrace();
        }

        if( mIntervalDuration == -1 ) {
            Log.i( LOG_TAG, CLASS + ": startSensor: mIntervalDuration == -1. Not scheduling a stopSensor" );
            return;
        }

        final Handler handler = new Handler();
        handler.postDelayed( new Runnable() {
            @Override
            public void run() {
                Log.i( LOG_TAG, CLASS + ": startSensor: PostDelayed: calling stopSensor" );
                stopSensor();
            }
        }, mIntervalDuration );
    }

    private void stopSensor( ) {
        Log.i( LOG_TAG, CLASS + ": stopSensor: called." );

        if( mSensor == null ) {
            Log.d( LOG_TAG, CLASS + ": stopSensor: mSensor == null" );
            return;
        }

        mSensor.unregisterListener();
        if( mTimeBetweenIntervals == -1 ) {
            Log.i( LOG_TAG, CLASS + ": stopSensor: mTimeBetweenIntervals == -1. Not scheduling a startSensor" );
            mLoggerSingleton.setStatus( LoggerSingleton.Status.STOPPED );
            return;
        }

        mLoggerSingleton.setStatus( LoggerSingleton.Status.PAUSED );
        final Handler handler = new Handler();
        handler.postDelayed( new Runnable() {
            @Override
            public void run() {
                Log.d( LOG_TAG, CLASS + ": stopSensor: PostDelayed: called" );
                startSensor();
            }
        }, mTimeBetweenIntervals );
    }

    private void removeSensor( ) {
        if( mSensor != null ) {
            mSensor.unregisterListener();
            mSensor = null;
            mLoggerSingleton.setStatus( LoggerSingleton.Status.STOPPED );
        }
    }

    @Override
    public void onPause() {
        Log.d( LOG_TAG, CLASS + ": onPause: called." );
//        mLoggerSingleton.setStatus( LoggerSingleton.Status.PAUSED );
        removeSensor();
        closeStream();
    }

    @Override
    public void onDestroy() {
        Log.d( LOG_TAG, CLASS + ": onDestroy: called." );
//        mLoggerSingleton.setStatus( LoggerSingleton.Status.STOPPED );
        removeSensor();
        closeStream();
    }
}
