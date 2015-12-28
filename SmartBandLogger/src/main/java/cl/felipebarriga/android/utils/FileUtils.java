package cl.felipebarriga.android.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Copyright (c) 2015 Felipe Barriga Richards. See Copyright Notice in LICENSE file.
 */
public class FileUtils {

    private static final String LOG_TAG = "SmartBandLogger";
    private static final String CLASS = "FileUtils";

    public static int getFileLines( File file ) {
        if( !file.canRead() || !file.isFile() ) {
            Log.w( LOG_TAG, CLASS + ": getLinesInFile: invalid file." );
            return -1;
        }

        int lines = 0;
        try {
            BufferedReader reader = new BufferedReader( new FileReader( file ) );
            while( reader.readLine() != null ) lines++;
            reader.close();
        } catch( Exception e ) {
            e.printStackTrace();
        }

        return lines;
    }
}
