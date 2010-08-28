package com.abi.profiles;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.util.Log;

import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;

import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.app.Dialog;
import android.os.Parcelable;
import android.net.Uri;

public class Profiles extends Activity implements OnClickListener, OnLongClickListener {
    //public static final int INSERT_ID = Menu.FIRST;
    private static final String DEBUG_TAG = "QuickProfiles";
    private static SettingHandler mSettingHandler;
    private static SettingHandler mHandler;
    private static Button mProfile1;
    private static Button mProfile2;
    private static Button mProfile3;
    private static Button mProfile4;
    private static Button mProfile5;

    public static final String EXTRA_KEY = "com.abi.profiles.extra";
    
    public static final String WIDGET_INTENT = "com.abi.profiles.WidgetAction";
    public static final String WIDGET_EXTRA_KEY = "com.abi.profiles.WidgetExtra";


    private static final int HELP_DIALOG_ID = 1;
    public static final int NUMBER_OF_PROFILES = 5;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(DEBUG_TAG, "Trying to set content view widget should work [Profiles]");
        setContentView(R.layout.profile_list);

        // Set up all our button listeners
        mProfile1 = (Button) findViewById(R.id.Profile1);
        mProfile2 = (Button) findViewById(R.id.Profile2);
        mProfile3 = (Button) findViewById(R.id.Profile3);
        mProfile4 = (Button) findViewById(R.id.Profile4);
        mProfile5 = (Button) findViewById(R.id.Profile5);



        // set our click listeners
        mProfile1.setOnClickListener(this);
        mProfile1.setOnLongClickListener(this);
        mProfile2.setOnClickListener(this);
        mProfile2.setOnLongClickListener(this);
        mProfile3.setOnClickListener(this);
        mProfile3.setOnLongClickListener(this);
        mProfile4.setOnClickListener(this);
        mProfile4.setOnLongClickListener(this);
        mProfile5.setOnClickListener(this);
        mProfile5.setOnLongClickListener(this);
        // done with that

    }

    public void onResume(){
        super.onResume();
        Log.i(DEBUG_TAG, "Trying to resume [Profiles]");

        // set our button text values
        mHandler = new SettingHandler(this, 0);
        mHandler.mWindow = getWindow();
        mHandler.mDbHelper.open();
        setProfileNames();

        mHandler.setProf(0);
        String profString = mHandler.getSetting(SettingHandler.SettingsEnum.TITLE) ;
        mHandler.mDbHelper.close();

        //Log.i(DEBUG_TAG, "Trying to set pressed "+profString+" [Profiles]");
        int profile = profString==null ? 0: Integer.valueOf(profString);
        switch (profile){
            case 0:
                mProfile1.setPressed(false);
                mProfile2.setPressed(false);
                mProfile3.setPressed(false);
                mProfile4.setPressed(false);
                mProfile5.setPressed(false);
                break;
            case 1:
                mProfile1.setPressed(true);
                mProfile2.setPressed(false);
                mProfile3.setPressed(false);
                mProfile4.setPressed(false);
                mProfile5.setPressed(false);
                break;
            case 2:
                mProfile1.setPressed(false);
                mProfile2.setPressed(true);
                mProfile3.setPressed(false);
                mProfile4.setPressed(false);
                mProfile5.setPressed(false);
                break;
            case 3:
                mProfile1.setPressed(false);
                mProfile2.setPressed(false);
                mProfile3.setPressed(true);
                mProfile4.setPressed(false);
                mProfile5.setPressed(false);
                break;
            case 4:
                mProfile1.setPressed(false);
                mProfile2.setPressed(false);
                mProfile3.setPressed(false);
                mProfile4.setPressed(true);
                mProfile5.setPressed(false);
                break;
            case 5:
                mProfile1.setPressed(false);
                mProfile2.setPressed(false);
                mProfile3.setPressed(false);
                mProfile4.setPressed(false);
                mProfile5.setPressed(true);
                break;
        }
    }

    private void setProfileNames(){
        //Log.i(DEBUG_TAG, "Setting profile names [Profiles]");
        for (int profile =1; profile <= NUMBER_OF_PROFILES; profile++){
            mHandler.setProf(profile);
            String text = mHandler.getSetting(SettingHandler.SettingsEnum.PROFILE_NAME);
            if (text == null){
                mHandler.setSetting(SettingHandler.SettingsEnum.PROFILE_NAME);
                text = mHandler.getSetting(SettingHandler.SettingsEnum.PROFILE_NAME);
            }
            // TODO fix this code to allow for any number of profiles
            if (!text.equals("-1")){
                switch(profile){
                    case 1:
                        mProfile1.setText(text);
                        break;
                    case 2:
                        mProfile2.setText(text);
                        break;
                    case 3:
                        mProfile3.setText(text);
                        break;
                    case 4:
                        mProfile4.setText(text);
                        break;
                    case 5:
                        mProfile5.setText(text);
                        break;
                }
            }
        }
        //Log.i(DEBUG_TAG, "Finished doing that [Profiles]");
    }

    public void onClick(View v){
        int profNum = -1;

        switch (v.getId()){
            case R.id.Profile1:
                profNum = 1;
                break;
            case R.id.Profile2:
                profNum = 2;
                break;
            case R.id.Profile3:
                profNum = 3;
                break;
            case R.id.Profile4:
                profNum = 4;
                break;
            case R.id.Profile5:
                profNum = 5;
                break;
        }

        mHandler.mDbHelper.open();

        // first set our TITLE value, so we know which profile is set.
        // this corresponds to profile 0.
        mHandler.setProf(0);
        mHandler.writeSetting(SettingHandler.SettingsEnum.TITLE, String.valueOf(profNum));

        // now set all our setting values
        mHandler.setProf(profNum);
        mHandler.setProfile();

        mHandler.mDbHelper.close();
        finish();
    }
    
    public boolean onLongClick(View v) {
        Intent i = new Intent(this,ProfileList.class);
        int profNum = 0;
        switch (v.getId()) {
            case R.id.Profile1:
                i.putExtra(EXTRA_KEY,1);
                profNum = 1;
                break;
            case R.id.Profile2:
                i.putExtra(EXTRA_KEY,2);
                profNum = 2;
                break;
            case R.id.Profile3:
                i.putExtra(EXTRA_KEY,3);
                profNum = 3;
                break;
            case R.id.Profile4:
                i.putExtra(EXTRA_KEY,4);
                profNum = 4;
                break;
            case R.id.Profile5:
                i.putExtra(EXTRA_KEY,5);
                profNum = 5;
                break;
        }

        mHandler.mDbHelper.open();
        mHandler.writeSetting(SettingHandler.SettingsEnum.TITLE, String.valueOf(profNum));
        mHandler.mDbHelper.close();
        startActivity(i);
        return true;
    }

}
