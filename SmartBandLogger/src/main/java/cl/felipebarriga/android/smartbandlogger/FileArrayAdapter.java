package cl.felipebarriga.android.smartbandlogger;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import cl.felipebarriga.android.utils.FileUtils;

/**
 * Copyright (c) 2015 Felipe Barriga Richards. See Copyright Notice in LICENSE file.
 */
public class FileArrayAdapter extends ArrayAdapter<File> implements View.OnCreateContextMenuListener, View.OnClickListener {

    private static final String LOG_TAG = "SmartBandLogger";
    private final String CLASS = getClass().getSimpleName();

    private final Context mContext;
    private final List<File> mFiles;

    @Override
    public void onCreateContextMenu( ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo ) {
        Log.d( LOG_TAG, CLASS + ": onCreateContextMenu: called" );

        menu.setHeaderTitle( "Actions" );
        int position = ( int ) v.getTag();

        menu.add( position, Common.ContextMenuActions.OPEN.ordinal(), Menu.NONE, "Open" );
        menu.add( position, Common.ContextMenuActions.SHARE.ordinal(), Menu.NONE, "Share" );
        menu.add( position, Common.ContextMenuActions.DELETE.ordinal(), Menu.NONE, "Delete" );
    }

    @Override
    public void onClick( View sender ) {
        Log.d( LOG_TAG, CLASS + ": onClick: called" );

        sender.setOnCreateContextMenuListener( this );
        sender.showContextMenu();
    }

    class ModifiedDateComparator implements Comparator<File> {
        @Override
        public int compare(File a, File b) {
            if( a.lastModified() > b.lastModified() ) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    public FileArrayAdapter( Context context, List<File> files ) {
        super( context, R.layout.list_file, files );
        Log.d( LOG_TAG, CLASS + ": Constructor: called" );

        mContext = context;
        mFiles = files;
        Collections.sort( mFiles, new ModifiedDateComparator() );
    }

    public File getFile( int position ) {
        return mFiles.get( position );
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
        LayoutInflater inflater = ( LayoutInflater ) mContext
                .getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        View rowView = inflater.inflate( R.layout.list_file, parent, false );

        File file = mFiles.get( position );
        if( file == null || !file.isFile() ) {
            Log.e( LOG_TAG, CLASS + ": getView: position=" + position + " Invalid file" );
            return rowView;
        }

        TextView filenameText = ( TextView ) rowView.findViewById( R.id.list_filename_txt );
        TextView recordsText  = ( TextView ) rowView.findViewById( R.id.list_records_txt );
        TextView dateText     = ( TextView ) rowView.findViewById( R.id.list_creation_date_text );

        ImageView imageView = ( ImageView ) rowView.findViewById( R.id.list_icon );
        String filename = file.getName();
        filenameText.setText( filename );

        String extension;
        try {
            extension = filename.split( Pattern.quote( "." ) )[1];
        } catch( Exception e ) {
            Log.w( LOG_TAG, CLASS + ": getView: error retrieving extension. filename=" + filename );
            e.printStackTrace();
            extension = "undefined";
        }

        if( extension.equals( "csv" ) ) {
            imageView.setImageResource( R.drawable.csv_icon );
            recordsText.setText( getRecordString( file ) );
            dateText.setText( getFirstRecordDateString( file ) );
        } else if( extension.equals( "ods" ) ) {
            imageView.setImageResource( R.drawable.ods_icon );
            recordsText.setText( ( file.length() / 1024 ) + " KiB" );
            dateText.setText( timestamp2date( file.lastModified() ) );
        } else {
            imageView.setImageResource( R.drawable.unknown_icon );
            recordsText.setText( ( file.length() / 1024 ) + " KiB" );
            dateText.setText( timestamp2date( file.lastModified() ) );
        }

        rowView.setTag( position );
        rowView.setOnClickListener( this );

        return rowView;
    }

    private String timestamp2date( long ts ) {
        SimpleDateFormat sdf = new SimpleDateFormat(
                mContext.getString( R.string.show_logs_date_format) );
        return sdf.format( new Date( ts ) );
    }

    private String getFirstRecordDateString( File file ) {

        if( !file.canRead() || !file.isFile() ) {
            Log.w( LOG_TAG, CLASS + ": getFirstRecordDateString: invalid file." );
            return null;
        }

        String dateStr = "couldn't retrieve date";
        try {
            BufferedReader reader = new BufferedReader( new FileReader( file ) );
            reader.readLine();
            String secondLine = reader.readLine();
            reader.close();

            String timestampStr = secondLine.split( "," )[0];
            long timestamp = Long.parseLong( timestampStr );

            dateStr = timestamp2date( timestamp );
            Log.d( LOG_TAG, CLASS + ": timestamp="  + timestamp
                    + " timestampStr=" + timestampStr + " dateStr=" + dateStr );

        } catch( Exception e ) {
            e.printStackTrace();
        }

        return dateStr;
    }

    private String getRecordString( File file ) {
        String fileLinesStr;
        int fileLines = FileUtils.getFileLines( file );
        if( fileLines < 0 ) {
            fileLinesStr = "Unable to retrieve records.";
        } else {
            fileLinesStr = ( fileLines - 1 ) + " records";
        }
        return fileLinesStr;
    }
}
