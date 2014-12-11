package com.abi.profiles;

import android.app.Activity;

import android.content.ContentResolver;
import android.content.Context;

import android.database.SQLException;

import android.util.Log;

import android.media.AudioManager;

import android.net.wifi.WifiManager;

import android.provider.Settings.Secure;
import android.provider.Settings.System;



import android.view.Window;
import android.view.WindowManager;

public class SettingHandler{
    // TODO Make this class a singleton. 
    private Context mCx;
    private static ContentResolver mCr;
    private int mProfNum;
    public static ProfilesDbHelper mDbHelper;
    public static Window mWindow;
    private static final int NUMBER_OF_SETTINGS = 5;

    //private static final SettingsEnum ALL_SETTINGS;
    private static final String DEBUG_TAG = "QuickProfiles";
    public static boolean mBluetoothNewSdk;
    static {
        try {
            //Log.i(DEBUG_TAG, "Checking if bluetoothadapter is available [SettingsHandler]");
            BluetoothHandler.checkAvailable();
            //Log.i(DEBUG_TAG, "Success");
            mBluetoothNewSdk = true;
        }
        catch (Throwable t) {
            //Log.i(DEBUG_TAG, "Not available [SettingHandler]");
            mBluetoothNewSdk = false;
        }
    }

    // from android.provider.Settings
    public static final String NOTIFICATIONS_USE_RING_VOLUME = "notifications_use_ring_volume";

    public static AudioManager mAudioManager;

    public SettingHandler(Context cx, int profNum){
        mCx = cx;
        mCr = mCx.getContentResolver();
        setProf(profNum);
        mDbHelper = new ProfilesDbHelper(mCx);
        mAudioManager = (AudioManager) mCx.getSystemService(mCx.AUDIO_SERVICE);

        if (mAudioManager == null) {
            //Log.e(DEBUG_TAG, "It seems we were unable to get an AudioManager instance for some reason [SettingHandler]");
        }

    }

    public String getDefaults(SettingsEnum setting){
        String value = "-1";
        //Log.i(DEBUG_TAG, "Setting default value for "+setting+"  [SettingHandler]");
        switch (setting) {
            case BRIGHTNESS:
                // If the setting doesn't exists, retrieve the current system
                // value, and write it to our DB
                value = System.getString(mCr, System.SCREEN_BRIGHTNESS);
                //Log.i(DEBUG_TAG, "Read "+value+" as screen brightness from system [SettingHandler]");
                writeSetting(SettingsEnum.BRIGHTNESS, value);
                break;
            case RINGER:
                value = String.valueOf(mAudioManager.getRingerMode());

                //Log.i(DEBUG_TAG, "Ringer caught the exception. [SettingHandler]");
                if (Integer.valueOf(value) == mAudioManager.RINGER_MODE_SILENT){
                    value = "0";
                }
                else {
                    value ="1";
                }

                writeSetting(SettingsEnum.RINGER, value);
                break;
            case VIBRATE:
                //Log.i(DEBUG_TAG, "Trying to read value... [SettingHandler]");
                value = String.valueOf(mAudioManager.getRingerMode());

                if (Integer.valueOf(value) == mAudioManager.RINGER_MODE_SILENT){
                    value = "0";
                }
                else if (Integer.valueOf(value) == mAudioManager.RINGER_MODE_VIBRATE){
                    value = "1";
                }
                else if (Integer.valueOf(value) == mAudioManager.RINGER_MODE_NORMAL){
                    value = "3";
                }
                //Log.i(DEBUG_TAG, "Storing "+value+" [Settinghandler]");
                writeSetting(SettingsEnum.VIBRATE, value);
                break;
            case RINGER_VOLUME:
                value = String.valueOf(mAudioManager.getStreamVolume(mAudioManager.STREAM_RING));
                //Log.i(DEBUG_TAG, "Read "+value+" from system [SettingHandler]");
                writeSetting(SettingsEnum.RINGER_VOLUME, value);
                break;
            case NOTIFICATION_VOLUME:
                value = String.valueOf(mAudioManager.getStreamVolume(mAudioManager.STREAM_NOTIFICATION));
                writeSetting(SettingsEnum.NOTIFICATION_VOLUME, value);
                break;
            case MEDIA_VOLUME:
                value = String.valueOf(mAudioManager.getStreamVolume(mAudioManager.STREAM_MUSIC));
                writeSetting(SettingsEnum.MEDIA_VOLUME, value);
                break;
            case ALARM_VOLUME:
                value = String.valueOf(mAudioManager.getStreamVolume(mAudioManager.STREAM_ALARM));
                writeSetting(SettingsEnum.ALARM_VOLUME, value);
                break;
            case VOICE_VOLUME:
                value = String.valueOf(mAudioManager.getStreamVolume(mAudioManager.STREAM_VOICE_CALL));
                writeSetting(SettingsEnum.VOICE_VOLUME, value);
                break;
            case SYSTEM_VOLUME:
                value = String.valueOf(mAudioManager.getStreamVolume(mAudioManager.STREAM_SYSTEM));
                writeSetting(SettingsEnum.SYSTEM_VOLUME, value);
                break;
            case BLUETOOTH:
                value = Secure.getString(mCr, Secure.BLUETOOTH_ON);
                writeSetting(SettingsEnum.BLUETOOTH, value);
                break;
            case WIFI:
                WifiManager wifiManager = (WifiManager) mCx.getSystemService(mCx.WIFI_SERVICE);
                //Log.i(DEBUG_TAG, "Checking wifi...[SettingHandler] "+wifiManager.isWifiEnabled());
                if (wifiManager.isWifiEnabled()){
                    //Log.i(DEBUG_TAG, "It's enabled...[SettingHandler]");
                    value = "1";
                }
                else {
                    //Log.i(DEBUG_TAG, "It's disabled...[SettingHandler]");
                    value = "0";
                }
                writeSetting(SettingsEnum.WIFI, value);
                break;
            case NOTIFICATION_BIND:
                //Log.i(DEBUG_TAG, "Checking current bind status [SettingHandler];");
                value = System.getString(mCr, NOTIFICATIONS_USE_RING_VOLUME);
                //Log.i(DEBUG_TAG, "Writing "+(value == null ? "0" : value)+" as bind setting [SettingHandler]");
                writeSetting(SettingsEnum.NOTIFICATION_BIND, value==null ? "0" : value);
                break;
            case PROFILE_NAME:
                value = "-1";
                writeSetting(SettingsEnum.PROFILE_NAME, value);
                break;
            case SHOW_HELP:
                value = "1";
                writeSetting(SettingsEnum.SHOW_HELP, value);
                break;
        }
        //Log.i(DEBUG_TAG, "Returning "+value+" [SettingHandler]");
        return value;
    }

    public void setProfile(){
        for (SettingsEnum item: SettingsEnum.values()){
            if (item == SettingsEnum.TITLE) continue;
            if (item == SettingsEnum.NUMBER_OF_PROFILES) continue;
            if (item == SettingsEnum.SHOW_HELP) continue;
            setSetting(item);
        }
    }

    public void setProfile(SettingsEnum setting){
        setSetting(setting);
    }

    public int setSetting(SettingsEnum setting){
        String value="-1";
        //Log.i(DEBUG_TAG, "setSetting called with "+setting+" [SettingHandler]");
        //Log.i(DEBUG_TAG, " ");
        //Log.i(DEBUG_TAG, " ");
        //Log.i(DEBUG_TAG, " ");
        //Log.i(DEBUG_TAG, " ");
        //Log.i(DEBUG_TAG, " ");

        value = getSetting(setting);

        //Log.i(DEBUG_TAG, "Got the value of "+value+" ProfileList]");
        if (value == null){
            value = getDefaults(setting);
        }
        // This is the nitty gritty, where all the settings are actually set.
        //if (value.equals("-1")){
            //Log.e(DEBUG_TAG, "The Value is -1!!! This should NEVER happen! FIXME now!!!!");
            //Log.e(DEBUG_TAG, "^ This is for setting" + setting +" [SettingHandler]");
        //}
        // so we know when we can change the volume settings
        String vibration;
        String ring;
        Boolean success  = null;

        switch(setting){
            case BRIGHTNESS:
                System.putString(mCr, System.SCREEN_BRIGHTNESS, value);
                if (mWindow instanceof Window){
                    WindowManager.LayoutParams lp = mWindow.getAttributes();
                    //Log.i(DEBUG_TAG, "Setting brightness to "+(Integer.valueOf(value) )+" [SettingHandler]");
                    lp.screenBrightness = Integer.valueOf(value) / 255.0f;
                    mWindow.setAttributes(lp);
                }
                break;
            case RINGER:
                if (value.equals("0")){
                    //Log.i(DEBUG_TAG, "Setting silent [SettingHandler]");
                    mAudioManager.setRingerMode(mAudioManager.RINGER_MODE_SILENT);
                }
                else {
                    // Set to "normal" mode and let VIBRATE setting dictate whether or
                    // not vibrate should be on.
                    //Log.i(DEBUG_TAG, "Setting normal [SettingHandler]");
                    mAudioManager.setRingerMode(mAudioManager.RINGER_MODE_NORMAL);
                }
                //Log.i(DEBUG_TAG, "Break [SettingHandler]");
                break;
            case VIBRATE:
                String ringer = getSetting(SettingsEnum.RINGER);
                if (ringer.equals("1") ){
                    switch (Integer.valueOf(value)){
                        case 0:
                            mAudioManager.setRingerMode(mAudioManager.RINGER_MODE_SILENT);
                            break;
                        case 1:
                            mAudioManager.setRingerMode(mAudioManager.RINGER_MODE_VIBRATE);
                            break;
                        case 2:
                            mAudioManager.setRingerMode(mAudioManager.RINGER_MODE_NORMAL);
                            mAudioManager.setVibrateSetting(mAudioManager.VIBRATE_TYPE_RINGER,mAudioManager.VIBRATE_SETTING_OFF);
                            break;
                        case 3:
                            mAudioManager.setRingerMode(mAudioManager.RINGER_MODE_NORMAL);
                            mAudioManager.setVibrateSetting(mAudioManager.VIBRATE_TYPE_RINGER,mAudioManager.VIBRATE_SETTING_ON);
                            break;
                    }
                }
                break;
            case RINGER_VOLUME:
                vibration = getSetting(SettingsEnum.VIBRATE);
                ring = getSetting(SettingsEnum.RINGER);
                if (vibration.equals("0") || ring.equals("0")){
                    //Log.i(DEBUG_TAG, "Setting the vibCheck to false [SettingHandler]");
                    //Log.i(DEBUG_TAG, "No value was set for ringer volume [SettingHandler]");
                    //Log.i(DEBUG_TAG, "vibration = "+vibration+" and ring = "+ring+" [SettingHandler]");
                }
                else{
                    //Log.i(DEBUG_TAG, "Setting ring volume to "+value+" [SettingHandler]");
                    mAudioManager.setStreamVolume(mAudioManager.STREAM_RING, Integer.valueOf(value), 0);
                }
                break;
            case NOTIFICATION_VOLUME:
                vibration = getSetting(SettingsEnum.VIBRATE);
                ring = getSetting(SettingsEnum.RINGER);
                if (vibration.equals("0") || ring.equals("0")){
                    //Log.i(DEBUG_TAG, "Setting the vibCheck to false [SettingHandler]");
                }
                else{
                    //Log.i(DEBUG_TAG, "Setting the vibCheck to True [SettingHandler]");
                    mAudioManager.setStreamVolume(mAudioManager.STREAM_NOTIFICATION, Integer.valueOf(value), 0);
                }
                break;
            case MEDIA_VOLUME:
                vibration = getSetting(SettingsEnum.VIBRATE);
                ring = getSetting(SettingsEnum.RINGER);
                if (vibration.equals("0") || ring.equals("0")){
                    //Log.i(DEBUG_TAG, "Setting the vibCheck to false [SettingHandler]");
                }
                else{
                    //Log.i(DEBUG_TAG, "Setting the vibCheck to True [SettingHandler]");
                    mAudioManager.setStreamVolume(mAudioManager.STREAM_MUSIC, Integer.valueOf(value), 0);
                }
                break;
            case ALARM_VOLUME:
                vibration = getSetting(SettingsEnum.VIBRATE);
                ring = getSetting(SettingsEnum.RINGER);
                if (vibration.equals("0") || ring.equals("0")){
                    //Log.i(DEBUG_TAG, "Setting the vibCheck to false [SettingHandler]");
                }
                else{
                    //Log.i(DEBUG_TAG, "Setting the vibCheck to True [SettingHandler]");
                    mAudioManager.setStreamVolume(mAudioManager.STREAM_ALARM, Integer.valueOf(value), 0);
                }
                break;
            case VOICE_VOLUME:
                vibration = getSetting(SettingsEnum.VIBRATE);
                ring = getSetting(SettingsEnum.RINGER);
                if (vibration.equals("0") || ring.equals("0")){
                    //Log.i(DEBUG_TAG, "Setting the vibCheck to false [SettingHandler]");
                }
                else{
                    //Log.i(DEBUG_TAG, "Setting the vibCheck to True [SettingHandler]");
                    mAudioManager.setStreamVolume(mAudioManager.STREAM_VOICE_CALL, Integer.valueOf(value), 0);
                }
                break;
            case SYSTEM_VOLUME:
                vibration = getSetting(SettingsEnum.VIBRATE);
                ring = getSetting(SettingsEnum.RINGER);
                if (vibration.equals("0") || ring.equals("0")){
                    //Log.i(DEBUG_TAG, "Setting the vibCheck to false [SettingHandler]");
                }
                else{
                    //Log.i(DEBUG_TAG, "Setting the vibCheck to True [SettingHandler]");
                    mAudioManager.setStreamVolume(mAudioManager.STREAM_SYSTEM, Integer.valueOf(value), 0);
                }
                break;
            case BLUETOOTH:
                if (mBluetoothNewSdk) {
                    //Log.i(DEBUG_TAG, "Trying to get an adapter [SettingHandler]");
                    BluetoothHandler btAdapter = new BluetoothHandler();
                    if (btAdapter.mBtAdapter == null) {
                        //Log.i(DEBUG_TAG, "Returning -1 [SettingHandler]");
                        return -1;
                    }
                    switch (Integer.valueOf(value)){
                        case 0:
                            //Log.i(DEBUG_TAG, "Trying to disable [SettingHandler]");
                            success = btAdapter.disable();
                            //if (success) Log.i(DEBUG_TAG, "Success [SettingHandler]");
                            //else Log.i(DEBUG_TAG, "Fail [SettingHandler]");
                            break;
                        case 1:
                            //Log.i(DEBUG_TAG, "Trying to enable [SettingHandler]");
                            success = btAdapter.enable();
                            //if (success) Log.i(DEBUG_TAG, "Success [SettingHandler]");
                            //else Log.i(DEBUG_TAG, "Fail [SettingHandler]");
                            break;
                    }
                }
                else {
                    //Log.i(DEBUG_TAG, "No 2.0 help [SettingHandler]");
                    OldBluetoothHandler btAdapter;
                    try {
                        btAdapter = new OldBluetoothHandler(mCx);
                    }
                    catch (Throwable e) {
                        //Log.e(DEBUG_TAG, "Bluetooth not available, pre 2.0 [SettingHandler]");
                        return -1;
                    }
                    switch (Integer.valueOf(value)){
                        case 0:
                            //Log.i(DEBUG_TAG, "Trying to disable bluetooth the old way [SettingHandler]");
                            btAdapter.setEnabled(false);
                            break;
                        case 1:
                            btAdapter.setEnabled(true);
                            break;
                    }
                }
                break;
            case WIFI:
                WifiManager wifiManager = (WifiManager) mCx.getSystemService(mCx.WIFI_SERVICE);
                switch (Integer.valueOf(value)){
                    case 0:
                        //Log.i(DEBUG_TAG, "Disabling Wifi [SettingHandler]");
                        success = wifiManager.setWifiEnabled(false);
                        //if (success) Log.i(DEBUG_TAG, "Successful [SettingHandler]");
                        //else Log.i(DEBUG_TAG, "Fail [SettingHandler]");
                        break;
                    case 1:
                        //Log.i(DEBUG_TAG, "Enabling Wifi [SettingHandler]");
                        success = wifiManager.setWifiEnabled(true);
                        //if (success) Log.i(DEBUG_TAG, "Successful [SettingHandler]");
                        //else Log.i(DEBUG_TAG, "Fail [SettingHandler]");
                        break;
                }
            case NOTIFICATION_BIND:
                //Log.i(DEBUG_TAG, "Putting "+value+" as bind value in system [SettingHandler]");
                System.putString(mCr, NOTIFICATIONS_USE_RING_VOLUME, value);
                break;
            //case GPS:
                //break;
            case SHOW_HELP:
                // do nothing
                break;

        }
        //Log.e(DEBUG_TAG, "This should not happen. [SettingHandler]");
        return 0;
    }

    public void writeSetting(SettingsEnum setting, String value){
        //Log.i(DEBUG_TAG, "Write called with "+setting+" "+value+" for profile "+mProfNum+" [SettingHandler]");
        mDbHelper.open();
        switch (setting){
            case BRIGHTNESS:
                //Log.i(DEBUG_TAG, "I am setting "+value+" to profile "+mProfNum);
                mDbHelper.createSetting(mProfNum, System.SCREEN_BRIGHTNESS, value);
                break;
            case RINGER:
                mDbHelper.createSetting(mProfNum, mDbHelper.RINGER, value);
                break;
            case VIBRATE:
                //Log.i(DEBUG_TAG, "Creating setting for vibrate with value of "+value+" [SettingHandler]");
                mDbHelper.createSetting(mProfNum, mDbHelper.VIBRATE, value);
                break;
            case RINGER_VOLUME:
                //Log.i(DEBUG_TAG, "Storing ring volume [SettingHandler]");
                mDbHelper.createSetting(mProfNum, mDbHelper.RINGER_VOLUME, value);
                break;
            case NOTIFICATION_VOLUME:
                mDbHelper.createSetting(mProfNum, mDbHelper.NOTIFICATION_VOLUME, value);
                break;
            case MEDIA_VOLUME:
                mDbHelper.createSetting(mProfNum, mDbHelper.MEDIA_VOLUME, value);
                break;
            case ALARM_VOLUME:
                mDbHelper.createSetting(mProfNum, mDbHelper.ALARM_VOLUME, value);
                break;
            case VOICE_VOLUME:
                //Log.i(DEBUG_TAG, "Set voice volume to "+value+" [SettingHandler]");
                mDbHelper.createSetting(mProfNum, mDbHelper.VOICE_VOLUME, value);
                break;
            case SYSTEM_VOLUME:
                //Log.i(DEBUG_TAG, "Set system volume to "+value+" [SettingHandler]");
                mDbHelper.createSetting(mProfNum, mDbHelper.SYSTEM_VOLUME, value);
                break;
            case BLUETOOTH:
                mDbHelper.createSetting(mProfNum, mDbHelper.BLUETOOTH, value);
                break;
            case WIFI:
                //Log.i(DEBUG_TAG, "Writing to Wifi "+value+" [SettingHandler]");
                mDbHelper.createSetting(mProfNum, mDbHelper.WIFI, value);
                break;
            case NOTIFICATION_BIND:
                //Log.i(DEBUG_TAG, "Storing "+value+" as bind setting [SettingHandler]");
                mDbHelper.createSetting(mProfNum, mDbHelper.NOTIFICATION_BIND, value);
                break;
            case NUMBER_OF_PROFILES:
                mDbHelper.createSetting(mProfNum, mDbHelper.NUMBER_OF_PROFILES, value);
                break;
            //case GPS:
                //mDbHelper.createSetting(mProfNum, mDbHelper.GPS, value);
                //break;
            case SHOW_HELP:
                mDbHelper.createSetting(mProfNum, mDbHelper.SHOW_HELP, value);
                break;
            case TITLE:
                //Log.i(DEBUG_TAG, "Writing "+value+" to TITLE [SettingHandler]");
                mDbHelper.createSetting(mProfNum, mDbHelper.TITLE, value);
                break;
            case PROFILE_NAME:
                //Log.i(DEBUG_TAG, "Writing to profile name "+value+" [SettingHandler]");
                mDbHelper.createSetting(mProfNum, mDbHelper.PROFILE_NAME, value);
                break;

        }
        mDbHelper.close();
    }

    public String getSetting(SettingsEnum setting){
        //Log.i(DEBUG_TAG, "Read called with "+setting+" [SettingHandler]");
        String value = null;
        mDbHelper.open();
        switch(setting){
            case BRIGHTNESS:
                //Log.i(DEBUG_TAG, "fetching brightness setting [SettingHandler]");
                value =  mDbHelper.fetchSetting(mProfNum, System.SCREEN_BRIGHTNESS);
                break;
            case RINGER:
                //Log.i(DEBUG_TAG, "fetching ringer setting [SettingHandler]");
                value =  mDbHelper.fetchSetting(mProfNum, mDbHelper.RINGER);
                break;
            case VIBRATE:
                //Log.i(DEBUG_TAG, "returning vibrate setting [SettingHandler]");
                value =  mDbHelper.fetchSetting(mProfNum, mDbHelper.VIBRATE);
                break;
            case RINGER_VOLUME:
                //Log.i(DEBUG_TAG, "Returing ringer volume [SettingHandler]");
                value =  mDbHelper.fetchSetting(mProfNum, mDbHelper.RINGER_VOLUME);
                break;
            case NOTIFICATION_VOLUME:
                value =  mDbHelper.fetchSetting(mProfNum, mDbHelper.NOTIFICATION_VOLUME);
                break;
            case MEDIA_VOLUME:
                value =  mDbHelper.fetchSetting(mProfNum, mDbHelper.MEDIA_VOLUME);
                break;
            case ALARM_VOLUME:
                value =  mDbHelper.fetchSetting(mProfNum, mDbHelper.ALARM_VOLUME);
                break;
            case VOICE_VOLUME:
                value =  mDbHelper.fetchSetting(mProfNum, mDbHelper.VOICE_VOLUME);
                break;
            case SYSTEM_VOLUME:
                value =  mDbHelper.fetchSetting(mProfNum, mDbHelper.SYSTEM_VOLUME);
                break;
            case BLUETOOTH:
                value =  mDbHelper.fetchSetting(mProfNum, mDbHelper.BLUETOOTH);
                break;
            case WIFI:
                //Log.i(DEBUG_TAG, "Returning Wifi state [SettingHandler]");
                value =  mDbHelper.fetchSetting(mProfNum, mDbHelper.WIFI);
                break;
            case NOTIFICATION_BIND:
                //Log.i(DEBUG_TAG, "Returning bind state [SettingHandler]");
                value =  mDbHelper.fetchSetting(mProfNum, mDbHelper.NOTIFICATION_BIND);
                break;
            //case GPS:
                //value =  mDbHelper.fetchSetting(mProfNum, mDbHelper.GPS);
            case SHOW_HELP:
                //Log.i(DEBUG_TAG, "Trying to return setting");
                value =  mDbHelper.fetchSetting(mProfNum, mDbHelper.SHOW_HELP);
                break;
            case TITLE:
                //Log.i(DEBUG_TAG, "Returning from title name "+mDbHelper.fetchSetting(mProfNum, mDbHelper.TITLE)+" [SettingHandler]");
                value =  mDbHelper.fetchSetting(mProfNum, mDbHelper.TITLE);
                break;
            case PROFILE_NAME:
                value = (mDbHelper.fetchSetting(mProfNum, mDbHelper.PROFILE_NAME));
                break;

        }
        if (value == null){
            value = getDefaults(setting);
        }
        //Log.i(DEBUG_TAG, "Returning "+value+" [SettingHandler]");
        mDbHelper.close();
        return value;
    }

    public void setProf(int profNum){
        //Log.i(DEBUG_TAG, "Profile number set to "+profNum+" [Settinghandler]");
        mProfNum = profNum;
    }

    public int getProf(){
        return mProfNum;
    }

    public void addProfile(){
        Integer maxProf = Integer.valueOf(this.getSetting(SettingsEnum.NUMBER_OF_PROFILES));
        if (maxProf == null){
            this.writeSetting(SettingsEnum.NUMBER_OF_PROFILES, String.valueOf(6));
        }
        else{
            this.writeSetting(SettingsEnum.NUMBER_OF_PROFILES, String.valueOf(maxProf+1));
        }
    }

    enum SettingsEnum {
        BRIGHTNESS,
        RINGER,
        VIBRATE,
        RINGER_VOLUME,
        NOTIFICATION_VOLUME,
        MEDIA_VOLUME,
        ALARM_VOLUME,
        VOICE_VOLUME,
        SYSTEM_VOLUME,
        BLUETOOTH,
        NOTIFICATION_BIND,
        WIFI,
        TITLE,
        PROFILE_NAME,
        NUMBER_OF_PROFILES,
        //GPS,
        SHOW_HELP
    }
}
