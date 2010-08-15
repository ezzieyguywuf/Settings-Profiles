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
    private static SettingHandler titleHandler;
    private static Window mCurrentWindow;
    private static Button mProfile1;
    private static Button mProfile2;
    private static Button mProfile3;
    private static Button mProfile4;
    private static Button mProfile5;

    private static final int HELP_DIALOG_ID = 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Log.i(DEBUG_TAG, "Trying to set content view [Profiles]");
        setContentView(R.layout.profile_list);

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
        mProfile1 = (Button) findViewById(R.id.Profile1);
        mProfile2 = (Button) findViewById(R.id.Profile2);
        mProfile3 = (Button) findViewById(R.id.Profile3);
        mProfile4 = (Button) findViewById(R.id.Profile4);
        mProfile5 = (Button) findViewById(R.id.Profile5);

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
        mCurrentWindow = getWindow();
        titleHandler = new SettingHandler(this, 0, mCurrentWindow);
        String profString = titleHandler.getSetting(SettingHandler.SettingsEnum.TITLE) ;
        Log.i(DEBUG_TAG, "Trying to set pressed "+profString+" [Profiles]");
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

        Log.i(DEBUG_TAG, "Trying to write "+String.valueOf(profNum)+" [Profiles]");
        titleHandler.writeSetting(SettingHandler.SettingsEnum.TITLE, String.valueOf(profNum));

        for (SettingHandler.SettingsEnum item: SettingHandler.SettingsEnum.values()){
            //Log.i(DEBUG_TAG, "Loading the setting for "+item+" [ProfileList]");
            if (item == SettingHandler.SettingsEnum.TITLE) continue;
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
