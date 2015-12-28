package cl.felipebarriga.android.smartbandlogger;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2015 Felipe Barriga Richards. See Copyright Notice in LICENSE file.
 */
public class ShowLogsActivity extends ListActivity {

    private static final String LOG_TAG = "SmartBandLogger";
    private final String CLASS = getClass().getSimpleName();

    private FileArrayAdapter mFileArrayAdapter = null;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        Log.d( LOG_TAG, CLASS + ": onCreate: called" );

        mFileArrayAdapter = new FileArrayAdapter( this, getFileList() );
        setListAdapter( mFileArrayAdapter );
    }

    @Override
    public boolean onContextItemSelected( MenuItem item ) {
        Common.ContextMenuActions action =
                Common.ContextMenuActions.values()[item.getItemId()];

        Log.d( LOG_TAG, CLASS + ": onContextItemSelected action=" + action.name()
                + " groupId=" + item.getGroupId() );
        File file = mFileArrayAdapter.getFile( item.getGroupId() );
        if( !file.setReadable( true, false ) ) {
            Log.w( LOG_TAG, CLASS + ": onContextItemSelected: Error fixing file permissions" );
        }

        Log.d( LOG_TAG, CLASS + ": onContextItemSelected file=" + file.getName() );

        switch( action ) {
            case OPEN:
                Intent viewIntent = new Intent();
                viewIntent.setAction( Intent.ACTION_VIEW );
                viewIntent.setDataAndType( Uri.fromFile( file ), "text/*" );
                startActivity( viewIntent );
                break;

            case SHARE:
                Intent sendIntent = new Intent();
                sendIntent.setAction( Intent.ACTION_SEND );
                sendIntent.putExtra( Intent.EXTRA_STREAM, Uri.fromFile( file ) );
                sendIntent.setType( "text/*" );
                startActivity( sendIntent );
                break;

            case DELETE:
                // FIXME: Refresh list + warn if error
                boolean deleted = file.delete();
                break;

            default:
                break;
        }

        return true;
    }

    public List<File> getFileList() {
        Log.d( LOG_TAG, CLASS + ": getFileList: called" );

        File path = this.getFilesDir();
        File files[] = path.listFiles();
        List<File> filteredFiles = new ArrayList<>();

        if( files == null ) {
            Log.w( LOG_TAG, CLASS + ": populateFileList: Couldn't retrieve file list" );
            return filteredFiles;
        }

        for( File file : files ) {
            if( file == null || !file.isFile() ) {
                Log.w( LOG_TAG, CLASS + ": populateFileList: Invalid file (or directory?)" );
                continue;
            }

            filteredFiles.add( file );
        }

        Log.d( LOG_TAG, CLASS + ": getFileList: returning " + filteredFiles.size() + " files." );

        return filteredFiles;
    }

}
