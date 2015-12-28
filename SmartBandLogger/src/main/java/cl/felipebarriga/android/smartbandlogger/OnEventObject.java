package cl.felipebarriga.android.smartbandlogger;

import java.util.EventObject;

/**
 * Copyright (c) 2015 Felipe Barriga Richards. See Copyright Notice in LICENSE file.
 */
public class OnEventObject extends EventObject {
    public enum Actions {
        RECORDS,
        LOG_FILENAME,
        LOG_SIZE,
        STATUS,
        SENSOR_RATE,
        ALL
    }

    final private Actions mAction;
    public OnEventObject( Object source, Actions action ) {
        super( source );
        mAction = action;
    }

    public Actions getAction() {
        return mAction;
    }
}
