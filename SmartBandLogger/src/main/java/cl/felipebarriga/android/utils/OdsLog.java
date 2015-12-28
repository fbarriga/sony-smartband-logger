package cl.felipebarriga.android.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Copyright (c) 2015 Felipe Barriga Richards. See Copyright Notice in LICENSE file.
 */
public class OdsLog {
    public enum ROW_TYPE {
        FLOAT,
        DATE
    };

    class TypedRow {
        private final String mValue;
        private final ROW_TYPE mType;

        TypedRow( String value, ROW_TYPE type ) {
            mValue = value;
            mType = type;
        }

        public String toString() {
            String ret;
            if( mType == ROW_TYPE.FLOAT ) {
                ret = "<table:table-cell calcext:value-type=\"float\" office:value=\""
                    + mValue + "\" office:value-type=\"float\" >"
                    + "<text:p>"
                        + mValue
                    + "</text:p>"
                    + "</table:table-cell>";

            } else if( mType == ROW_TYPE.DATE ) {
                // ISO 8601
                ret = "<table:table-cell calcext:value-type=\"date\" office:date-value=\""
                    + mValue // 2015-11-28T10:16:42Z / "2015-12-04T18:24:02"
                    + "\" office:value-type=\"date\">"
                    + "<text:p>"
                        + mValue // "2015-12-04T18:24:02.000"
                    + "</text:p>"
                    + "</table:table-cell>";
            } else {
                ret = "Error, unknown rot type! mType=" + mType;
            }

            return ret;
        }
    }

    class OdsData {
        private String[] mHeaders;
        private List<TypedRow[]> mData;

        OdsData() {
            mData = new ArrayList<>();
        }

        public String[] getHeaders() {
            return mHeaders;
        }

        public void setHeaders( String[] mHeaders ) {
            this.mHeaders = mHeaders;
        }

        public void addRecord( TypedRow[] data ) throws Exception {
            if( mHeaders.length == 0 ) {
                throw new Exception( "Headers need to be defined before adding data" );
            }

            if( mHeaders.length != data.length ) {
                throw new Exception( "Headers length must match data length" );
            }

            mData.add( data );
        }

        public List<TypedRow[]> getRecords() {
            return mData;
        }

    }

    private static final String LOG_TAG = "SmartBandLogger";
    private final String CLASS = getClass().getSimpleName();

    private final Context mContext;

    public OdsLog( Context mContext ) {
        this.mContext = mContext;
    }

    // TODO: Refactor, communicate with CsvLog to know the types of each field
    public String createLogFromCSV( String csvFilename ) throws Exception {
        Log.d( LOG_TAG, CLASS + ": createLogFromCSV: csvFilename=" + csvFilename );
        String odsFilename = csvFilename.split( Pattern.quote( "." ) )[0] + ".ods";
        if( odsFilename.length() <= 4 ) {
            odsFilename = UUID.randomUUID().toString() + ".ods";
            Log.e( LOG_TAG, CLASS + ": createLogFromCSV: Invalid filename. csvFilename=" + csvFilename  + " newFilename=" + odsFilename );
        }
        Log.d( LOG_TAG, CLASS + ": createLogFromCSV: odsFilename=" + odsFilename );

        OdsData odsData = new OdsData();

        InputStream is = mContext.getApplicationContext().openFileInput( csvFilename );
        InputStreamReader ir = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(ir);

        String line;
        int fields = 0;
        boolean firstLine = true;
        while( null != ( line = br.readLine() ) ) {
            String[] pieces = line.split( Pattern.quote( "," ) );
            if( firstLine ) { // skip csv headers
                fields = pieces.length;
                odsData.setHeaders( pieces );
                firstLine = false;
                continue;
            }

            if( pieces.length != fields ) {
                Log.w( LOG_TAG, CLASS + ": createLogFromCSV: the data doesn't match with the headers: "
                        + pieces + " fields=" + fields );
                continue;
            }

            TypedRow[] content = new TypedRow[fields];
            content[0] = new TypedRow( pieces[0], ROW_TYPE.FLOAT ); // timestamp
            content[1] = new TypedRow( pieces[1], ROW_TYPE.DATE );  // date
            content[2] = new TypedRow( pieces[2], ROW_TYPE.FLOAT ); // elapsed time
            content[3] = new TypedRow( pieces[3], ROW_TYPE.FLOAT ); // x
            content[4] = new TypedRow( pieces[4], ROW_TYPE.FLOAT ); // y
            content[5] = new TypedRow( pieces[5], ROW_TYPE.FLOAT ); // z
            content[6] = new TypedRow( pieces[6], ROW_TYPE.FLOAT ); // accuracy

            odsData.addRecord( content );
        }
        is.close();
        Log.d( LOG_TAG, CLASS + ": createLogFromCSV: odsData generated" );

        generateOdf( odsFilename, odsData );

        return odsFilename;
    }

    private void generateOdf( String filename, OdsData odsData ) throws Exception {
        Log.d( LOG_TAG, CLASS + ": generateOdf: called. filename=" + filename );

        FileOutputStream fos = mContext.openFileOutput( filename, Context.MODE_PRIVATE );
        ZipOutputStream zos = new ZipOutputStream( new BufferedOutputStream( fos ) );

        String content;
        ZipEntry ze;

        /* mimetype must be the first file, without atttributes nor compression:
         * http://www.jejik.com/articles/2010/03/how_to_correctly_create_odf_documents_using_zip/
         */
        content = getAssetFile( "ods/mimetype" );
        ze = new ZipEntry( "mimetype" );
        zos.setMethod( ZipEntry.STORED );
        CRC32 crc32 = new CRC32();
        byte[] buffer = content.getBytes();
        crc32.update( buffer );
        ze.setCrc( crc32.getValue() );
        ze.setSize( buffer.length );
        ze.setCompressedSize( buffer.length );
        zos.putNextEntry( ze );
        zos.write( buffer );
        zos.flush();
        zos.closeEntry();
        zos.setMethod( ZipEntry.DEFLATED );


        content = getAssetFile( "ods/settings.xml" );
        ze = new ZipEntry( "settings.xml" );
        zos.putNextEntry( ze );
        zos.write( content.getBytes() );
        zos.closeEntry();

        content = getAssetFile( "ods/styles.xml" );
        ze = new ZipEntry( "styles.xml" );
        zos.putNextEntry( ze );
        zos.write( content.getBytes() );
        zos.closeEntry();

        content = getAssetFile( "ods/META-INF/manifest.xml" );
        ze = new ZipEntry( "META-INF/manifest.xml" );
        zos.putNextEntry( ze );
        zos.write( content.getBytes() );
        zos.closeEntry();

        content = generateMeta( getAssetFile( "ods/meta.xml" ) );
        ze = new ZipEntry( "meta.xml" );
        zos.putNextEntry( ze );
        zos.write( content.getBytes() );
        zos.closeEntry();

        byte[] thumbnailBytes = getAssetFileBytes( "ods/Thumbnails/thumbnail.png" );
        ze = new ZipEntry( "Thumbnails/thumbnail.png" );
        zos.putNextEntry( ze );
        zos.write( thumbnailBytes );
        zos.closeEntry();
        thumbnailBytes = null;

        content = generateContent( getAssetFile( "ods/content.xml" ), odsData );
        ze = new ZipEntry( "content.xml" );
        zos.putNextEntry( ze );
        zos.write( content.getBytes() );
        zos.closeEntry();
        content = null;

        zos.close();
    }

    private byte[] getAssetFileBytes( String filename ) throws Exception {
        InputStream is = mContext.getAssets().open( filename );
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();

        return buffer;
    }

    private String getAssetFile( String filename ) throws Exception {
        InputStream is = mContext.getAssets().open( filename );
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();

        return new String( buffer );
    }

    private String generateMeta( String metaContent ) throws Exception {
        String versionName = mContext.getPackageManager().getPackageInfo(
                mContext.getPackageName(), 0).versionName;


        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss" );
        String creationDate = sdf.format( new Date() );

        metaContent = metaContent.replace( "${GENERATOR}", "SmartBandLogger/" + versionName );
        metaContent = metaContent.replace( "${DC_DATE}", creationDate );
        metaContent = metaContent.replace( "${CREATION_DATE}", creationDate );

        return metaContent;
    }

    private String generateContent( String content, OdsData odsData ) throws Exception {
        Log.d( LOG_TAG, CLASS + ": generateContent: called" );

        String buffer = new String();
        String[] headers = odsData.getHeaders();

        for( String header : headers ) {
            buffer += "<table:table-cell calcext:value-type=\"string\" office:value-type=\"string\">"
                    + "<text:p>"
                        + header
                    + "</text:p>"
                    + "</table:table-cell>\n";
        }
        content = content.replace( "${HEADERS}", buffer );
        Log.d( LOG_TAG, CLASS + ": generateContent: headers generated" );

        // TODO: not all columns should have the same width. it's possible to set it automatically ?
        buffer = new String();
        for( int i = 0; i < headers.length; ++i ) {
            buffer += "<style:style style:family=\"table-column\" style:name=\"co" + i + "\">"
                    + "<style:table-column-properties fo:break-before=\"auto\" style:column-width=\"120.00pt\"/>"
                    + "</style:style>";
        }
        content = content.replace( "${COLUMN_STYLES}", buffer );
        Log.d( LOG_TAG, CLASS + ": generateContent: style generated" );


        StringBuilder bodyStrBuilder = new StringBuilder( );
        List<TypedRow[]> rows = odsData.getRecords();
        for( TypedRow[] row : rows ) {
            bodyStrBuilder.append( "<table:table-row table:style-name=\"ro2\">" );
            for( TypedRow record : row ) {
                bodyStrBuilder.append( record.toString() );
            }
            bodyStrBuilder.append( "</table:table-row>" );
        }
        Log.d( LOG_TAG, CLASS + ": generateContent: body generated, replacing it" );

        content = content.replace( "${BODY}", bodyStrBuilder.toString() );
        Log.d( LOG_TAG, CLASS + ": generateContent: body replaced" );

        return content;
    }
}
