package com.abi.profiles;

import android.app.Activity;

import android.bluetooth.BluetoothAdapter;

import android.content.ContentResolver;
import android.content.Context;

import android.database.SQLException;

import android.media.AudioManager;

import android.net.wifi.WifiManager;

import android.provider.Settings.Secure;
import android.provider.Settings.System;

import android.util.Log;

import android.view.Window;
import android.view.WindowManager;

public class SettingHandler{
    private Context mCx;
    private static ContentResolver mCr;
    private static int mProfNum;
    public static ProfilesDbHelper mDbHelper;
    private static Window mWindow;
    private static final int NUMBER_OF_SETTINGS = 5;

    //private static final SettingsEnum ALL_SETTINGS;
    private static final String DEBUG_TAG = "QuickProfiles";
    

    // from android.provider.Settings
    public static final String NOTIFICATIONS_USE_RING_VOLUME = "notifications_use_ring_volume";

    public static AudioManager mAudioManager;

    public SettingHandler(Context cx, int profNum, Window window){
        mCx = cx;
        mCr = mCx.getContentResolver();
        mProfNum = profNum;
        mDbHelper = new ProfilesDbHelper(mCx);
        mDbHelper.open();
        mWindow = window;
        mAudioManager = (AudioManager) mCx.getSystemService(mCx.AUDIO_SERVICE);

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
                //Log.i(DEBUG_TAG, "Writing "+value+" as bind setting [SettingHandler]");
                writeSetting(SettingsEnum.NOTIFICATION_BIND, value);
                break;
        }
        return value;
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

        if (value == null){
            value = getDefaults(setting);
        }
        // This is the nitty gritty, where all the settings are actually set.
        //Log.i(DEBUG_TAG, "Finally [SettingHandler]");
        if (value.equals("-1")){
            //Log.e(DEBUG_TAG, "The Value is -1!!! This should NEVER happen! FIXME now!!!!");
            //Log.e(DEBUG_TAG, "^ This is for setting" + setting +" [SettingHandler]");
        }
        // so we know when we can change the volume settings
        String vibration;
        String ring;
        Boolean success  = null;

        switch(setting){
            case BRIGHTNESS:
                System.putString(mCr, System.SCREEN_BRIGHTNESS, value);
                WindowManager.LayoutParams lp = mWindow.getAttributes();
                //Log.i(DEBUG_TAG, "Setting brightness to "+(Integer.valueOf(value) )+" [SettingHandler]");
                lp.screenBrightness = Integer.valueOf(value) / 255.0f;
                mWindow.setAttributes(lp);
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
                BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                //Log.i(DEBUG_TAG, "breaking");
                //Log.i(DEBUG_TAG, "Setting bluetooth state [SettingHandler]");
                if (btAdapter == null){
                    //Log.e(DEBUG_TAG, "Seems that bluetooth is not supported for this device");
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
            //case SHOW_HELP:
                // do nothing
                //break;

        }
        //Log.e(DEBUG_TAG, "This should not happen. [SettingHandler]");
        return 0;
    }

    public void writeSetting(SettingsEnum setting, String value){
        //Log.i(DEBUG_TAG, "Write called with "+setting+" "+value+" [SettingHandler]");
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
            //case GPS:
                //mDbHelper.createSetting(mProfNum, mDbHelper.GPS, value);
                //break;
            //case SHOW_HELP:
                //mDbHelper.createSetting(mProfNum, mDbHelper.SHOW_HELP, value);
                //break;

        }
    }

    public String getSetting(SettingsEnum setting){
        //Log.i(DEBUG_TAG, "Read called with "+setting+" [SettingHandler]");
        switch(setting){
            case BRIGHTNESS:
                //Log.i(DEBUG_TAG, "fetching brightness setting [SettingHandler]");
                return mDbHelper.fetchSetting(mProfNum, System.SCREEN_BRIGHTNESS);
            case RINGER:
                //Log.i(DEBUG_TAG, "fetching ringer setting [SettingHandler]");
                return mDbHelper.fetchSetting(mProfNum, mDbHelper.RINGER);
            case VIBRATE:
                //Log.i(DEBUG_TAG, "returning vibrate setting [SettingHandler]");
                return mDbHelper.fetchSetting(mProfNum, mDbHelper.VIBRATE);
            case RINGER_VOLUME:
                //Log.i(DEBUG_TAG, "Returing ringer volume [SettingHandler]");
                return mDbHelper.fetchSetting(mProfNum, mDbHelper.RINGER_VOLUME);
            case NOTIFICATION_VOLUME:
                return mDbHelper.fetchSetting(mProfNum, mDbHelper.NOTIFICATION_VOLUME);
            case MEDIA_VOLUME:
                return mDbHelper.fetchSetting(mProfNum, mDbHelper.MEDIA_VOLUME);
            case ALARM_VOLUME:
                return mDbHelper.fetchSetting(mProfNum, mDbHelper.ALARM_VOLUME);
            case VOICE_VOLUME:
                return mDbHelper.fetchSetting(mProfNum, mDbHelper.VOICE_VOLUME);
            case SYSTEM_VOLUME:
                return mDbHelper.fetchSetting(mProfNum, mDbHelper.SYSTEM_VOLUME);
            case BLUETOOTH:
                return mDbHelper.fetchSetting(mProfNum, mDbHelper.BLUETOOTH);
            case WIFI:
                //Log.i(DEBUG_TAG, "Returning Wifi state [SettingHandler]");
                return mDbHelper.fetchSetting(mProfNum, mDbHelper.WIFI);
            case NOTIFICATION_BIND:
                //Log.i(DEBUG_TAG, "Returning bind state [SettingHandler]");
                return mDbHelper.fetchSetting(mProfNum, mDbHelper.NOTIFICATION_BIND);
            //case GPS:
                //return mDbHelper.fetchSetting(mProfNum, mDbHelper.GPS);
            //case SHOW_HELP:
                //Log.i(DEBUG_TAG, "Trying to return setting");
                //return mDbHelper.fetchSetting(mProfNum, mDbHelper.SHOW_HELP);

        }
        return "0";
    }

    public void setProf(int profNum){
        mProfNum = profNum;
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
        WIFI
        //GPS,
        //SHOW_HELP
    }
}
