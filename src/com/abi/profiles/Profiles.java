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

public class Profiles extends Activity implements OnClickListener, OnLongClickListener {
    //public static final int INSERT_ID = Menu.FIRST;
    private static final String DEBUG_TAG = "QuickProfiles";
    private static SettingHandler mSettingHandler;
    private static Window mCurrentWindow;

    private static final int HELP_DIALOG_ID = 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Log.i(DEBUG_TAG, "Trying to set content view [Profiles]");
        setContentView(R.layout.profile_list);
        mCurrentWindow = getWindow();

/*
 *        mCurrentWindow = getWindow();
 *        mSettingHandler = new SettingHandler(this, 0, mCurrentWindow);
 *
 *        Integer helpDialog = (mSettingHandler.getSetting(SettingHandler.SettingsEnum.SHOW_HELP)==null ? 0: 1);
 *        Log.i(DEBUG_TAG, "Checking helpDialog [Profiles]");
 *        if (helpDialog == 0){
 *            // show help dialog
 *            Log.i(DEBUG_TAG, "Firing dialog [Profiles]");
 *            showDialog(HELP_DIALOG_ID);
 *        }
 *        else {
 *            // don't show it
 *        }
 */

        // Set up all our button listeners
        Button profile1 = (Button) findViewById(R.id.Profile1);
        Button profile2 = (Button) findViewById(R.id.Profile2);
        Button profile3 = (Button) findViewById(R.id.Profile3);
        Button profile4 = (Button) findViewById(R.id.Profile4);
        Button profile5 = (Button) findViewById(R.id.Profile5);

        profile1.setOnClickListener(this);
        profile1.setOnLongClickListener(this);
        profile2.setOnClickListener(this);
        profile2.setOnLongClickListener(this);
        profile3.setOnClickListener(this);
        profile3.setOnLongClickListener(this);
        profile4.setOnClickListener(this);
        profile4.setOnLongClickListener(this);
        profile5.setOnClickListener(this);
        profile5.setOnLongClickListener(this);
        // done with that

    }
    /*
     *@Override
     *public boolean onCreateOptionsMenu(Menu menu) {
     *    boolean result = super.onCreateOptionsMenu(menu);
     *    menu.add(0, INSERT_ID, 0, R.string.menu_show);
     *    return result;
     *}
     */
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
        mSettingHandler = new SettingHandler(this, profNum, mCurrentWindow);

        for (SettingHandler.SettingsEnum item: SettingHandler.SettingsEnum.values()){
            //Log.i(DEBUG_TAG, "Loading the setting for "+item+" [ProfileList]");
            mSettingHandler.setSetting(item);
        }
        finish();
    }
    
    public boolean onLongClick(View v) {
        Intent i = new Intent(this,ProfileList.class);
        switch (v.getId()) {
            case R.id.Profile1:{
                i.putExtra("com.abi.profiles.ProfileNumber",1);
                break;
            }
            case R.id.Profile2:{
                i.putExtra("com.abi.profiles.ProfileNumber",2);
                break;
            }
            case R.id.Profile3:{
                i.putExtra("com.abi.profiles.ProfileNumber",3);
                break;
            }
            case R.id.Profile4:{
                i.putExtra("com.abi.profiles.ProfileNumber",4);
                break;
            }
            case R.id.Profile5:{
                i.putExtra("com.abi.profiles.ProfileNumber",5);
                break;
            }
        }
        startActivity(i);
        return true;
    }

    /*
     *public Dialog onCreateDialog(int id){
     *    Log.i(DEBUG_TAG, "onCreateDialog was called [ProfileList]");
     *    Dialog dialog = new Dialog(this);
     *    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
     *    dialog.setContentView(R.layout.help_dialog);
     *   
     *    Button done = (Button) dialog.findViewById(R.id.ok_help);
     *    Log.i(DEBUG_TAG, "Setting click listener [HelpDialog]");
     *    done.setOnClickListener(new View.OnClickListener(){
     *        @Override
     *        public void onClick(View v){
     *            finish();
     *        }
     *    });
     *    return dialog;
     *}
     */
}
