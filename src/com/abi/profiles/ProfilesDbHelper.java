package com.abi.profiles;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import android.util.Log;

public class ProfilesDbHelper{
    private static  Context mCtx;
    private static final String DATABASE_TABLE="profiles";
    private static final String KEY_PROFILENUM="profilenum";
    private static final String KEY_SETTING="setting";
    private static final String KEY_VALUE="value";
    private static final String KEY_ROWID="_id";
    private static final String DEBUG_TAG = "QuickProfiles";

    public static final String RINGER = "ring_on_off";
    public static final String VIBRATE = "vibrate_setting";
    public static final String RINGER_VOLUME = "ringer_volume";
    public static final String NOTIFICATION_VOLUME = "notification_volume";
    public static final String MEDIA_VOLUME = "media_volume";
    public static final String ALARM_VOLUME = "alarm_volume";
    public static final String VOICE_VOLUME = "voice_volume";
    public static final String SYSTEM_VOLUME = "system_volume";
    public static final String BLUETOOTH = "bluetooth_onoff";
    public static final String NOTIFICATION_BIND = "notification_bind";
    public static final String WIFI = "wifi_onoff";
    public static final String GPS = "gps_onoff";
    public static final String SHOW_HELP = "show_help_yesno";
    public static final String TITLE = "title";

    // note: most of this was copied from the Android notepad tutorial
    // so look there if you don't get it.
    private static final String DATABASE_CREATE =
        "create table profiles (profilenum integer not null, setting string not null, value string not null, UNIQUE(profilenum, setting) ON CONFLICT REPLACE);";
    private static final String DATABASE_NAME = "quickprofiles";
    private static final int DATABASE_VERSION = 1;

    private static final String TAG = "ProfilesDbAdapter";
    private static DatabaseHelper mDbHelper;
    private static SQLiteDatabase mDb;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    //+ newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }
    // end all stuf to  create database
    ProfilesDbHelper(Context ctx){
        this.mCtx = ctx;
    }

    /**
     * Open the settings database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure. This was also taken from the notepad tutorial.
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public ProfilesDbHelper open() throws SQLiteException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();

        return this;
    }
    public void close() {
        mDbHelper.close();
    }

    /**
     * Create a new Setting-value pair. If the pair is successfully created
     * return the new rowId for that note, otherwise return a -1 to indicate
     * failure.
     * 
     * @param setting the setting to store a value for
     * @param value the actual value for the setting
     * @return rowId or -1 if failed
     */
    public long createSetting(int profilenum, String setting, String value) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_PROFILENUM, profilenum);
        initialValues.put(KEY_SETTING, setting);
        initialValues.put(KEY_VALUE, value);
        // TODO change UNIQUE to PRIMARY_KEY for performance boost?
        return mDb.insert(DATABASE_TABLE,null, initialValues);
    }


      /**
     * Return a Cursor positioned at the setting that matches the given profile
     * number and setting type.
     * 
     * @param whichProfile the profile number (int) that we are working with
     * @param whichSetting the setting that you wish to work with
     * @return Cursor positioned to matching setting, if found
     * @throws SQLException if setting could not be found/retrieved
     */
    public String fetchSetting(int whichProfile, String whichSetting) throws SQLiteException {
        String sqlWhere = KEY_SETTING + " = \"" + whichSetting + "\" AND " + KEY_PROFILENUM + " = " + whichProfile;
        String outputValue = null;
        Cursor localCursor;

        //Log.i(DEBUG_TAG, "I am trying now to query with \""+sqlWhere+"\"");
        //Log.i(DEBUG_TAG, "   and heres some more info "+DATABASE_TABLE+" and "+KEY_VALUE);
        localCursor = mDb.query(DATABASE_TABLE, new String[] {KEY_VALUE}, sqlWhere, null, null, null, null); 
        //Log.i(DEBUG_TAG, "Want to get here.");
        if (localCursor.moveToFirst()){
            //Log.i(DEBUG_TAG, "Looks like our cursor has values...");
            outputValue = localCursor.getString(localCursor.getColumnIndex(KEY_VALUE));
            localCursor.close();
            return outputValue;
        }
        else{
            //Log.i(DEBUG_TAG, "It seems the cursor was empty for "+whichSetting+" . Set the default value will you? [ProfilesDbHelper]");
            localCursor.close();
            //Log.i(DEBUG_TAG, "Returning null [ProfilesDbHelper]");
            return null;
        }
    }  


}
