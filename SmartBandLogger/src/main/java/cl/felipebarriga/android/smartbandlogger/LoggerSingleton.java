package cl.felipebarriga.android.smartbandlogger;

import android.util.Log;

import com.sonyericsson.extras.liveware.aef.sensor.Sensor;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2015 Felipe Barriga Richards. See Copyright Notice in LICENSE file.
 */
public class LoggerSingleton {

    public enum Status {
        RUNNING,
        PAUSED,
        STOPPED
    }

    private static final int MAX_LIST_SIZE = 5000;
    private static final int MAX_LIST_SIZE_DELTA = 500; // Clean every 500 appended records

    private static LoggerSingleton mInstance = null;

    private static final String LOG_TAG = "SmartBandLogger";
    private final String CLASS = getClass().getSimpleName();

    private OnEventListener mOnEventListener;

    private List<ChartRecord> mChartRecords = null;
    private int mSensorRate;
    private long mLogSize = 0;
    private long mRecords = 0;
    private long mStartTime = 0;
    private long mAccuTime = 0;
    private String mLogFilename = "";
    private Status mStatus = Status.STOPPED;

    private LoggerSingleton(){
    }

    public static LoggerSingleton getInstance(){
        if(mInstance == null)
        {
            mInstance = new LoggerSingleton();
            mInstance.mChartRecords = new ArrayList<>();
        }
        return mInstance;
    }

    public void setOnEventListener(OnEventListener listener) {
        mOnEventListener = listener;
    }

    private void notifyListener( OnEventObject.Actions action ) {
        if( mOnEventListener != null ) {
            mOnEventListener.onEvent( new OnEventObject( this, action ) );
        }
    }

    public void addChartRecord( long timestamp, float x , float y, float z ) {
        mChartRecords.add( new ChartRecord( timestamp, x, y, z ) );
        int recordsLen = mChartRecords.size();
        if( recordsLen > MAX_LIST_SIZE + MAX_LIST_SIZE_DELTA ) {
            mChartRecords.subList( 0, recordsLen - MAX_LIST_SIZE ).clear();
        }
    }

    public List<ChartRecord> getChartRecords() {
        return mChartRecords;
    }

    public void clearChartRecords() {
        mChartRecords.clear();
    }

    public void setRecords( long value ) {
        mRecords = value;
        notifyListener( OnEventObject.Actions.RECORDS );
    }

    public long getRecords() {
        return mRecords;
    }

    public String getLogFilename() {
        return mLogFilename;
    }

    public void setLogFilename( String mLogFilename ) {
        this.mLogFilename = mLogFilename;
        notifyListener( OnEventObject.Actions.LOG_FILENAME );
    }

    public long getLogSize() {
        return mLogSize;
    }

    public void setLogSize( long mLogSize ) {
        this.mLogSize = mLogSize;
        notifyListener( OnEventObject.Actions.LOG_SIZE );
    }

    public String getSensorRateStr() {
        String sensorRate = "unknown";
        switch( mSensorRate )
        {
            case Sensor.SensorRates.SENSOR_DELAY_FASTEST:
                sensorRate = "FASTEST";
                break;

            case Sensor.SensorRates.SENSOR_DELAY_GAME:
                sensorRate = "FAST";
                break;

            case Sensor.SensorRates.SENSOR_DELAY_NORMAL:
                sensorRate = "NORMAL";
                break;

            case Sensor.SensorRates.SENSOR_DELAY_UI:
                sensorRate = "SLOW";
                break;

            default:
                Log.w( LOG_TAG, CLASS + ": getSensorRateStr: unknown rate: " + mSensorRate );
                break;
        }

        return sensorRate;
    }

    public int getSensorRate() {
        return mSensorRate;
    }

    public void setSensorRate( int mSensorRate ) {
        this.mSensorRate = mSensorRate;
        notifyListener( OnEventObject.Actions.SENSOR_RATE );
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus( Status mStatus ) {
        if( this.mStatus == mStatus ){
            Log.d( LOG_TAG, CLASS + ": setStatus: status didn't change" );
        }

        if( mStatus == Status.STOPPED ) {
            mAccuTime += System.currentTimeMillis() - mStartTime;
            mStartTime = 0;
            this.mStatus = mStatus;
            notifyListener( OnEventObject.Actions.STATUS );
            return;
        }

        switch( this.mStatus ) {
            case STOPPED:
                if( mStatus == Status.RUNNING ) {
                    mAccuTime = 0;
                    mStartTime = System.currentTimeMillis();
                } else {
                    Log.d( LOG_TAG, CLASS + ": setStatus: invalid change. mStatus=" + mStatus.name()
                            + " this.mStatus=" + this.mStatus.name() );
                }
                break;

            case RUNNING:
                if( mStatus == Status.PAUSED ) {
                    mAccuTime += System.currentTimeMillis() - mStartTime;
                    mStartTime = 0;
                } else {
                    Log.d( LOG_TAG, CLASS + ": setStatus: invalid change. mStatus=" + mStatus.name()
                            + " this.mStatus=" + this.mStatus.name() );
                }
                break;

            case PAUSED:
                if( mStatus == Status.RUNNING ) {
                    mStartTime = System.currentTimeMillis();
                } else {
                    Log.d( LOG_TAG, CLASS + ": setStatus: invalid change. mStatus=" + mStatus.name()
                            + " this.mStatus=" + this.mStatus.name() );
                }
                break;

        }

        this.mStatus = mStatus;
        notifyListener( OnEventObject.Actions.STATUS );
    }

    // FIXME: This isn't working when stopped.
    public long getElapsedTime() {
        long elapsedMs = mAccuTime;
        if( this.mStatus == Status.RUNNING ) {
            elapsedMs += ( System.currentTimeMillis() - mStartTime );
        }
        return elapsedMs;
    }

}
