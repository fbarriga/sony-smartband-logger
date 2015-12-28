package cl.felipebarriga.android.smartbandlogger;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Copyright (c) 2015 Felipe Barriga Richards. See Copyright Notice in LICENSE file.
 */
public class SettingsActivity extends android.preference.PreferenceActivity {

    private static final String LOG_TAG = "SmartBandLogger";
    private final String CLASS = getClass().getSimpleName();

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        Log.d( LOG_TAG, CLASS + " onCreate: called" );
        super.onCreate( savedInstanceState );

        getFragmentManager()
                .beginTransaction()
                .replace( android.R.id.content, new MyPreferenceFragment() ).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate( final Bundle savedInstanceState ) {
            super.onCreate( savedInstanceState );
            PreferenceManager.setDefaultValues( getActivity(), R.xml.preferences, false );
            addPreferencesFromResource( R.xml.preferences );
        }
    }

}
