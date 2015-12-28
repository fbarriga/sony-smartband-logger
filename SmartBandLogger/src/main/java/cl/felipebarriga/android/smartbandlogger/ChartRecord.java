package cl.felipebarriga.android.smartbandlogger;

/**
 * Copyright (c) 2015 Felipe Barriga Richards. See Copyright Notice in LICENSE file.
 */
public class ChartRecord {
    final public long timestamp;
    final public float x;
    final public float y;
    final public float z;

    public ChartRecord( long timestamp, float x, float y, float z ) {
        this.timestamp = timestamp;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "ts=" + timestamp + " x= " + x + " y=" + y + " z=" + z;
    }
}
