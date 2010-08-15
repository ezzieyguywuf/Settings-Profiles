package com.abi.profiles;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;

import android.provider.Settings.System;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.bluetooth.BluetoothAdapter;
import android.net.wifi.WifiManager;
//TODO add - screen brightness
//         - ringer on/off
//         - volume control
//         - bluetooth on/off
//         - wifi on/off
//         - gps on/off

public class ProfileList extends Activity implements OnClickListener, OnSeekBarChangeListener, OnCheckedChangeListener{

    static int mProfNum=-1;
    public static SettingHandler mHandler;
    static Window mWindow;
    static Context mCx;

    private static final String DEBUG_TAG = "QuickProfiles";
    private static final SettingHandler.SettingsEnum ENUM_BRIGHTNESS = SettingHandler.SettingsEnum.BRIGHTNESS;
    private static final SettingHandler.SettingsEnum ENUM_RINGER = SettingHandler.SettingsEnum.RINGER;
    private static final SettingHandler.SettingsEnum ENUM_VIBRATE = SettingHandler.SettingsEnum.VIBRATE;
    public static final SettingHandler.SettingsEnum ENUM_RINGER_VOLUME = SettingHandler.SettingsEnum.RINGER_VOLUME;
    public static final SettingHandler.SettingsEnum ENUM_NOTIFICATION_VOLUME = SettingHandler.SettingsEnum.NOTIFICATION_VOLUME;
    public static final SettingHandler.SettingsEnum ENUM_MEDIA_VOLUME = SettingHandler.SettingsEnum.MEDIA_VOLUME;
    public static final SettingHandler.SettingsEnum ENUM_ALARM_VOLUME = SettingHandler.SettingsEnum.ALARM_VOLUME;
    public static final SettingHandler.SettingsEnum ENUM_VOICE_VOLUME = SettingHandler.SettingsEnum.VOICE_VOLUME;
    public static final SettingHandler.SettingsEnum ENUM_SYSTEM_VOLUME = SettingHandler.SettingsEnum.SYSTEM_VOLUME;
    private static final SettingHandler.SettingsEnum ENUM_BLUETOOTH = SettingHandler.SettingsEnum.BLUETOOTH;
    public static final SettingHandler.SettingsEnum ENUM_NOTIFICATION_BIND = SettingHandler.SettingsEnum.NOTIFICATION_BIND;
    private static final SettingHandler.SettingsEnum ENUM_WIFI = SettingHandler.SettingsEnum.WIFI;
    //private static final SettingHandler.SettingsEnum ENUM_GPS = SettingHandler.SettingsEnum.GPS;
    private static final int VIBRATE_DIALOG_ID = 1;
    private static final int VOLUME_DIALOG_ID = 2;
    private static final int SCREEN_BRIGHTNESS_OFFSET = 30;
    private static SeekBar mBrightnessLevel;
    private static Button mVibrate;
    private static ToggleButton mOnOff;
    private static ToggleButton mBluetooth;
    private static ToggleButton mWifi;
    //private static ToggleButton mGps;
    private static Toast mBrightnessToast;
    private static Dialog mDialog;
    private static ProfileList mProfileList;
    private BluetoothReceiver mBluetoothReceiver;
    private WifiReceiver mWifiReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        // Find out which profile we're dealing with. 
        mProfNum = extras.getInt("com.abi.profiles.ProfileNumber");
        this.setTitle(this.getString(R.string.profile)+": "+String.valueOf(mProfNum));
        // If we weren't passed the Prof Num, get it from the savedInstanceState
        if (mProfNum==0){
            mProfNum = savedInstanceState.getInt("com.abi.profiles.ProfileNumber");
            //Log.i(DEBUG_TAG, "Resorting to savedInstanceState. mProfNum = "+mProfNum);
        }

        //Log.i(DEBUG_TAG, "Trying to open the database [ProfileListe]");
        //Log.i(DEBUG_TAG, "It seems we opened it successfully [ProfileList]");
        mWindow = getWindow();
        mHandler = new SettingHandler(this, mProfNum, mWindow);
        mCx = this;

        setContentView(R.layout.settings);

        // This is for the brightness bar.
        mBrightnessLevel = (SeekBar) findViewById(R.id.brightness_setting);
        mBrightnessLevel.setMax(255-SCREEN_BRIGHTNESS_OFFSET); // 255 is the max, but we offset this by SCREEN_BRIGHTNESS_OFFSET 
                                                                // cuz we don't want the user to set the brightness to 0 
        mBrightnessLevel.setOnSeekBarChangeListener(this);

        // This is for the ringer and vibrate buttons
        mVibrate = (Button) findViewById(R.id.Popup);
        mOnOff = (ToggleButton) findViewById(R.id.OnOff);
        mVibrate.setOnClickListener(this);
        mOnOff.setOnCheckedChangeListener(this);
        
        // This is for volume control
        TextView volume_control = (TextView) findViewById(R.id.volume_popup);
        volume_control.setOnClickListener(this);
        
        // This is for Bluetooth button
        // TODO make it so that bluetooth is never updated again if its not present
        mBluetooth = (ToggleButton) findViewById(R.id.bluetooth);
        int check = mHandler.setSetting(ENUM_BLUETOOTH);
        if (check == -1){
            mBluetooth.setEnabled(false);
        }
        mBluetooth.setOnCheckedChangeListener(this);

        // This is for the WiFi button
        mWifi = (ToggleButton) findViewById(R.id.wifi);
        mWifi.setOnCheckedChangeListener(this);
        
        // Our broadcast receivers need this
        mProfileList = this;

        // This is for the GPS button
        //mGps = (ToggleButton) findViewById(R.id.gps);
        //mGps.setOnCheckedChangeListener(this);

    }
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        //Log.i(DEBUG_TAG, "Saving instance state [ProfileList]");
        outState.putInt("com.abi.profiles.ProfileNumber",mProfNum);
    }

    @Override
    public void onResume(){
        super.onResume();
        //Log.i(DEBUG_TAG, "Trying to resume [ProfileList]");
        mHandler.mDbHelper.open();

        mBluetoothReceiver = new BluetoothReceiver();
        IntentFilter bluetoothChanged = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBluetoothReceiver, bluetoothChanged);

        mWifiReceiver = new WifiReceiver();
        IntentFilter wifiChanged = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(mWifiReceiver, wifiChanged);

        loadSettings();
        //Log.i(DEBUG_TAG, "Success [ProfileList]");
    }

    @Override
    public void onPause(){
        mHandler.mDbHelper.close();
        //Log.i(DEBUG_TAG, "Unregistering receiver [ProfileListe]");
        unregisterReceiver(mBluetoothReceiver);
        unregisterReceiver(mWifiReceiver);
        super.onPause();
    }

    class BluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context cx, Intent intent){
            // Consider using strategy method here
            //Log.i(DEBUG_TAG, "We recieved the broadcast! "+intent.getAction()+" [ProfileList]");
            setBluetoothText();
        }
        
        public void setBluetoothText(){
            //Log.i(DEBUG_TAG, "setBluetoothText [ProfileList]");
            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            if (btAdapter instanceof BluetoothAdapter){
                TextView bluetooth_text = (TextView) mProfileList.findViewById(R.id.bluetooth_status);
                int state = btAdapter.getState();

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        bluetooth_text.setText(R.string.bluetooth_disabled);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        bluetooth_text.setText(R.string.bluetooth_disabling);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        bluetooth_text.setText(R.string.bluetooth_enabled);
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        bluetooth_text.setText(R.string.bluetooth_enabling);
                        break;
                }
            }

        }
    }

    class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context cx, Intent intent){
            //Log.i(DEBUG_TAG, "We received the broadcast! "+intent.getAction()+" [ProfileList]");

            TextView wifi_text = (TextView) mProfileList.findViewById(R.id.wifi_status);
            WifiManager wifiManager = (WifiManager) cx.getSystemService(cx.WIFI_SERVICE);
            int state = wifiManager.getWifiState();
            switch (state){
                case WifiManager.WIFI_STATE_DISABLED:
                    //Log.i(DEBUG_TAG, "Writing disabled [ProfilList]");
                    wifi_text.setText(R.string.wifi_disabled);
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    //Log.i(DEBUG_TAG, "Writing disabling [ProfilList]");
                    wifi_text.setText(R.string.wifi_disabling);
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    //Log.i(DEBUG_TAG, "Writing enabled [ProfilList]");
                    wifi_text.setText(R.string.wifi_enabled);
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    //Log.i(DEBUG_TAG, "Writing enabling [ProfilList]");
                    wifi_text.setText(R.string.wifi_enabling);
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    //Log.i(DEBUG_TAG, "Writing unknown [ProfilList]");
                    wifi_text.setText(R.string.wifi_unknown);
                    break;
            }
        }
    }



    private void loadSettings(){
        // Now go ahead and set all the settings to what our stored
        // values are.
        for (SettingHandler.SettingsEnum item: SettingHandler.SettingsEnum.values()){
            //Log.i(DEBUG_TAG, "Loading the setting for "+item+" [ProfileList]");
            if (item == SettingHandler.SettingsEnum.TITLE) continue;
            loadSettings(item);
        }
    }

    private void loadSettings(SettingHandler.SettingsEnum setting){
        //Log.i(DEBUG_TAG, " ");
        //Log.i(DEBUG_TAG, " ");
        //Log.i(DEBUG_TAG, "Setting "+setting+" [ProfileList]");
        mHandler.setSetting(setting);
        updateUI(setting);
    }

    private void updateUI(SettingHandler.SettingsEnum setting){
        //Log.i(DEBUG_TAG, "Updating UI [Setting Handler]");
        // Now that our settings are retrieved, update the UI.
        switch (setting){
            case BRIGHTNESS:
                mBrightnessLevel.setProgress(Integer.valueOf(mHandler.getSetting(ENUM_BRIGHTNESS))-SCREEN_BRIGHTNESS_OFFSET);
                break;
            case RINGER:
                mOnOff.setChecked(mHandler.getSetting(ENUM_RINGER).equals("1"));
                break;
            case VIBRATE:
                TextView vibrationSetting = (TextView) findViewById(R.id.value_of_vibration);

                switch (Integer.valueOf(mHandler.getSetting(ENUM_VIBRATE))){
                    case 0:
                        vibrationSetting.setText(R.string.silent);
                        break;
                    case 1:
                        vibrationSetting.setText(R.string.vibration_only);
                        break;
                    case 2:
                        vibrationSetting.setText(R.string.sound_only);
                        break;
                    case 3:
                        vibrationSetting.setText(R.string.sound_and_vibration);
                        break;
                }
                break;
            case BLUETOOTH:
                mBluetooth.setChecked(mHandler.getSetting(ENUM_BLUETOOTH).equals("1"));
                mBluetoothReceiver.setBluetoothText();
                break;
            case WIFI:
                //Log.i(DEBUG_TAG, "Update UI for wifi [ProfileList]");
                mWifi.setChecked(mHandler.getSetting(ENUM_WIFI).equals("1"));
                break;
        }
    }

    public void onClick(View v){
        ToggleButton toggleButton; //lets minimize the number of objects we create 

        switch (v.getId()){
            case R.id.Popup:
                // vibrate stuff;
                showDialog(VIBRATE_DIALOG_ID);
                break;
            case R.id.silent:
                //Log.i(DEBUG_TAG, "Set Silent [ProfileList]");
                mHandler.writeSetting(ENUM_VIBRATE, "0");
                loadSettings(ENUM_VIBRATE);
                mDialog.dismiss();
                break;
            case R.id.vibration_only:
                //Log.i(DEBUG_TAG, "Set vibration_only [ProfileList]");
                mHandler.writeSetting(ENUM_VIBRATE, "1");
                loadSettings(ENUM_VIBRATE);
                mDialog.dismiss();
                break;
            case R.id.sound_only:
                //Log.i(DEBUG_TAG, "Set sound_only [ProfileList]");
                mHandler.writeSetting(ENUM_VIBRATE, "2");
                loadSettings(ENUM_VIBRATE);
                mDialog.dismiss();
                break;
            case R.id.sound_and_vibration:
                //Log.i(DEBUG_TAG, "Set sound_and_vibration [ProfileList]");
                mHandler.writeSetting(ENUM_VIBRATE, "3");
                loadSettings(ENUM_VIBRATE);
                mDialog.dismiss();
                break;
            case R.id.volume_popup:
                showDialog(VOLUME_DIALOG_ID);
                break;
            case R.id.volume_done:
                int vibrateCheck = Integer.valueOf(mHandler.getSetting(ENUM_VIBRATE));
                if (mHandler.getSetting(ENUM_RINGER).equals("1") && vibrateCheck > 1){
                    String value; 
                    SeekBar temp;
                     
                    temp = (SeekBar) mDialog.findViewById(R.id.ringer_volume);
                    value = (String) String.valueOf(temp.getProgress());
                    mHandler.writeSetting(ENUM_RINGER_VOLUME, value);
                    loadSettings(ENUM_RINGER_VOLUME);

                    temp = (SeekBar) mDialog.findViewById(R.id.notification_volume);
                    value = (String) String.valueOf(temp.getProgress());
                    mHandler.writeSetting(ENUM_NOTIFICATION_VOLUME, value);
                    loadSettings(ENUM_NOTIFICATION_VOLUME);

                    temp = (SeekBar) mDialog.findViewById(R.id.media_volume);
                    value = (String) String.valueOf(temp.getProgress());
                    mHandler.writeSetting(ENUM_MEDIA_VOLUME, value);
                    loadSettings(ENUM_MEDIA_VOLUME);

                    temp = (SeekBar) mDialog.findViewById(R.id.alarm_volume);
                    value = (String) String.valueOf(temp.getProgress());
                    mHandler.writeSetting(ENUM_ALARM_VOLUME, value);
                    loadSettings(ENUM_ALARM_VOLUME);

                    temp = (SeekBar) mDialog.findViewById(R.id.voice_call_volume);
                    value = (String) String.valueOf(temp.getProgress());
                    mHandler.writeSetting(ENUM_VOICE_VOLUME, value);
                    loadSettings(ENUM_VOICE_VOLUME);

                    temp = (SeekBar) mDialog.findViewById(R.id.system_volume);
                    value = (String) String.valueOf(temp.getProgress());
                    //Log.i(DEBUG_TAG, "writing "+value+" for system volume [ProfileList]");
                    mHandler.writeSetting(ENUM_SYSTEM_VOLUME, value);
                    loadSettings(ENUM_SYSTEM_VOLUME);

                    loadSettings(ENUM_NOTIFICATION_BIND);

                }
                    mDialog.dismiss();
                break;
            case R.id.volume_cancel:
                // TODO consider adding a toast popup here, to tell them their savings weren't changed.
                mDialog.dismiss();
                break;

        }
    }
    public void onCheckedChanged (CompoundButton buttonview, boolean isChecked){
        //Log.i(DEBUG_TAG, "Something got <un>checked [ProfileList]");
        switch (buttonview.getId()){
            case R.id.notification_bind:
                //Log.i(DEBUG_TAG, "Writing "+ (isChecked ? "1" : "0" )+ " to notification_bind [ProfileList]");

                mHandler.writeSetting(ENUM_NOTIFICATION_BIND, isChecked ? "1" : "0");
                SeekBar notifVolume = (SeekBar) mDialog.findViewById(R.id.notification_volume);

                if (isChecked){
                    notifVolume.setEnabled(false);
                    SeekBar ringVolume = (SeekBar) mDialog.findViewById(R.id.ringer_volume);
                    notifVolume.setProgress(ringVolume.getProgress());
                }
                break;
            case R.id.OnOff:
                // onoff stuff;
                if (isChecked){
                    mHandler.writeSetting(ENUM_RINGER, "1");
                }
                else {
                    mHandler.writeSetting(ENUM_RINGER, "0");
                }
                loadSettings(ENUM_RINGER);
                loadSettings(ENUM_VIBRATE);
                break;
            case R.id.bluetooth:
                //Log.i(DEBUG_TAG, "Responding to bluetooth button click [ProfileList]");
                if (isChecked){
                    //Log.i(DEBUG_TAG, "Writing 1 [ProfileList]");
                    mHandler.writeSetting(ENUM_BLUETOOTH, "1");
                }
                else {
                    //Log.i(DEBUG_TAG, "Writing 0 [ProfileList]");
                    mHandler.writeSetting(ENUM_BLUETOOTH, "0");
                }
                loadSettings(ENUM_BLUETOOTH);
                //Log.i(DEBUG_TAG, "Breaking [ProfileList]");
                break;
            case R.id.wifi:
                //Log.i(DEBUG_TAG, "Recieved Wifi state changed [ProfileList]");
                if (isChecked){
                    //Log.i(DEBUG_TAG, "Button is clicked [ProfileList]");
                    mHandler.writeSetting(ENUM_WIFI,"1");
                }
                else {
                    //Log.i(DEBUG_TAG, "Button is not clicked [ProfileList]");
                    mHandler.writeSetting(ENUM_WIFI,"0");
                }
                loadSettings(ENUM_WIFI);
                break;
            /*
             *case R.id.gps:
             *    if (isChecked){
             *        mHandler.writeSetting(ENUM_GPS,"1");
             *    }
             *    else {
             *        mHandler.writeSetting(ENUM_GPS, "0");
             *    }
             *    loadSettings(ENUM_GPS);
             *    break;
             */
        }
    }
    public Dialog onCreateDialog(int id){
        //Log.i(DEBUG_TAG, "onCreateDialog was called [ProfileList]");
        myDialogs dialog = new myDialogs();

        mDialog = dialog.createDialog(this, id);
        return mDialog;

    }

    public void onPrepareDialog(int id, Dialog dg){
        // Set all values for the volume sliders
        // TODO I think we really do need to add the rest of these. Check that.
        //Log.i(DEBUG_TAG, "onPrepareDialog is called [ProfileList]");
        mDialog = dg;
        switch (id){
            case VOLUME_DIALOG_ID:
                // update dialog with set values
                SeekBar tempSeekBar;
                loadSettings(ENUM_RINGER_VOLUME);
                loadSettings(ENUM_NOTIFICATION_VOLUME);
                loadSettings(ENUM_MEDIA_VOLUME);
                loadSettings(ENUM_ALARM_VOLUME);
                loadSettings(ENUM_VOICE_VOLUME);
                loadSettings(ENUM_SYSTEM_VOLUME);

                String notification_bind = mHandler.getSetting(ENUM_NOTIFICATION_BIND);

                tempSeekBar = (SeekBar) dg.findViewById(R.id.ringer_volume);
                if (tempSeekBar == null){
                    //Log.e(DEBUG_TAG, "Looks like your tempSeekBar is null [ProfileList]");
                }
                tempSeekBar.setProgress(mHandler.mAudioManager.getStreamVolume(mHandler.mAudioManager.STREAM_RING));

                tempSeekBar = (SeekBar) dg.findViewById(R.id.notification_volume);
                if (notification_bind.equals("1")){
                    //Log.i(DEBUG_TAG, "Notification is bound to ringer [ProfileList]");
                    tempSeekBar.setProgress(mHandler.mAudioManager.getStreamVolume(mHandler.mAudioManager.STREAM_RING));
                }
                else {
                    tempSeekBar.setProgress(mHandler.mAudioManager.getStreamVolume(mHandler.mAudioManager.STREAM_NOTIFICATION));
                }
                tempSeekBar = (SeekBar) dg.findViewById(R.id.media_volume);
                tempSeekBar.setProgress(mHandler.mAudioManager.getStreamVolume(mHandler.mAudioManager.STREAM_MUSIC));
                tempSeekBar = (SeekBar) dg.findViewById(R.id.alarm_volume);
                tempSeekBar.setProgress(mHandler.mAudioManager.getStreamVolume(mHandler.mAudioManager.STREAM_ALARM));
                tempSeekBar = (SeekBar) dg.findViewById(R.id.voice_call_volume);
                tempSeekBar.setProgress(mHandler.mAudioManager.getStreamVolume(mHandler.mAudioManager.STREAM_VOICE_CALL));
                tempSeekBar = (SeekBar) dg.findViewById(R.id.system_volume);
                tempSeekBar.setProgress(mHandler.mAudioManager.getStreamVolume(mHandler.mAudioManager.STREAM_SYSTEM));

                tempSeekBar = null;
        }
    }

    public void onProgressChanged(SeekBar s, int level, boolean userInit){
        String strLevel = String.valueOf(level);
        TextView progress;
        switch(s.getId()){
            case R.id.brightness_setting:

                if (userInit){

                    level += SCREEN_BRIGHTNESS_OFFSET; // Offsett the value by SCREEN_BRIGHTNESS_OFFSET, to prevent a screen brightness of 0
                    strLevel = String.valueOf(level);
                    mHandler.writeSetting(ENUM_BRIGHTNESS, strLevel);
                    mBrightnessToast = (Toast) Toast.makeText(mCx, String.valueOf(level/255.0f), Toast.LENGTH_SHORT);
                    // TODO Fix this later
                    //mBrightnessToast.show();
                }
                //Log.i(DEBUG_TAG, "Setting brightness to "+level+" [ProfileList]");
                // update screen regardless of who changed the slider
                mHandler.setSetting(ENUM_BRIGHTNESS);
                break;
            case R.id.ringer_volume:
                progress = (TextView) mDialog.findViewById(R.id.ring_value);
                progress.setText(String.valueOf(level));

                String notification_bind = mHandler.getSetting(ENUM_NOTIFICATION_BIND);
                //Log.i(DEBUG_TAG, "the value of notification bind is "+notification_bind+" [ProfileList]");
                if (notification_bind.equals("1")){
                    progress = (TextView) mDialog.findViewById(R.id.notif_value);
                    progress.setText(String.valueOf(level));
                    SeekBar notif_volume = (SeekBar) mDialog.findViewById(R.id.notification_volume);
                    notif_volume.setProgress(level);
                }
                break;
            case R.id.notification_volume:
                progress = (TextView) mDialog.findViewById(R.id.notif_value);
                progress.setText(String.valueOf(level));
                break;
            case R.id.media_volume:
                progress = (TextView) mDialog.findViewById(R.id.media_value);
                progress.setText(String.valueOf(level));
                break;
            case R.id.alarm_volume:
                progress = (TextView) mDialog.findViewById(R.id.alarm_value);
                progress.setText(String.valueOf(level));
                break;
            case R.id.voice_call_volume:
                progress = (TextView) mDialog.findViewById(R.id.voice_value);
                progress.setText(String.valueOf(level));
                break;
            case R.id.system_volume:
                progress = (TextView) mDialog.findViewById(R.id.system_value);
                progress.setText(String.valueOf(level));
                break;
        }
    }

    public void onStartTrackingTouch(SeekBar s){
        // TODO: add a toaster popup showing the percent of screen brightness.
    }

    public void onStopTrackingTouch(SeekBar s){
        switch (s.getId()){
            case R.id.ringer_volume:
                break;
            case R.id.notification_volume:
                break;
            case R.id.media_volume:
                break;
            case R.id.alarm_volume:
                break;
            case R.id.voice_call_volume:
                break;
            case R.id.system_volume:
                break;
        }
    }
}

