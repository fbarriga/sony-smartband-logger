package cl.felipebarriga.android.utils;

import android.content.Context;
import android.util.Log;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import cl.felipebarriga.android.smartbandlogger.R;

/**
 * Copyright (c) 2015 Felipe Barriga Richards. See Copyright Notice in LICENSE file.
 */
public class CsvLog {

    private static final String CSV_HEADERS = "timestamp,date,elapsed,value1,value2,value3,accuracy\n";
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    private static final String LOG_TAG = "SmartBandLogger";
    private final String CLASS = getClass().getSimpleName();

    private FileOutputStream mOutputStream = null;
    private final Context mContext;
    private long mLogSize = 0;
    private long mRecords = 0;
    private String mFilename;
    private boolean mDebugMessages = false;
    private long firstTs = 0;
    private SimpleDateFormat mSimpleDateFormat;

    public CsvLog( Context mContext ) {
        this.mContext = mContext;
        this.mSimpleDateFormat = new SimpleDateFormat( DATE_FORMAT );
    }

    public String getFilename() {
        return mFilename;
    }

    public long getSize() {
        return mLogSize;
    }

    public long getRecordsSize() {
        return mRecords;
    }

    public void setDebugMessage( boolean value ) {
        mDebugMessages = value;
    }

    private void incrementLogSize( int value ) {
        mLogSize += value;
    }

    private void incrementRecords() {
        mRecords++;
    }

    public void write( long timestamp, float x, float y, float z, int accuracy ) {

        if( mOutputStream == null ) {
            Log.e( LOG_TAG, CLASS + ": write: Error, outputStream closed." );
            return;
        }

        if( firstTs == 0 ) {
            firstTs = timestamp;
        }

        long elapsedTime = timestamp - firstTs;
        String date = mSimpleDateFormat.format( new Date( timestamp ) );

        String log
                = timestamp     + ","
                + date          + ","
                + elapsedTime   + ","
                + x             + ","
                + y             + ","
                + z             + ","
                + accuracy
                + "\n";

        try {
            mOutputStream.write( log.getBytes() );
            incrementLogSize( log.length() );
            incrementRecords();
        } catch( Exception e ) {
            e.printStackTrace();
        }

        if( mDebugMessages ) {
            Log.d( LOG_TAG, CLASS + ": write: " + log );
        }
    }

    public void createLog( String filenamePrefix ) {
        Log.d( LOG_TAG, CLASS + ": createLog: filenamePrefix=" + filenamePrefix );

        SimpleDateFormat sdf = new SimpleDateFormat( mContext.getString( R.string.log_suffix_date_format) );
        mFilename = filenamePrefix
                + sdf.format( new Date( System.currentTimeMillis() ) )
                + ".csv";

        try {
            mOutputStream = mContext.getApplicationContext().openFileOutput( mFilename, Context.MODE_PRIVATE );
        } catch( Exception e ) {
            e.printStackTrace();
            closeStream();
            return;
        }

        mLogSize = 0;
        mRecords = 0;
        firstTs  = 0;

        try {
            this.mOutputStream.write( CSV_HEADERS.getBytes() );
            incrementLogSize( CSV_HEADERS.length() );
        } catch( Exception e ) {
            e.printStackTrace();
            closeStream();
        }

        Log.d( LOG_TAG, CLASS + ": createLog: done" );
    }

    public void closeStream() {
        Log.d( LOG_TAG, CLASS + ": closeStream" );

        if( mOutputStream == null ) {
            return;
        }

        try {
            mOutputStream.close();
        } catch( Exception e ) {
            e.printStackTrace();
        } finally {
            mOutputStream = null;
        }
    }

}
