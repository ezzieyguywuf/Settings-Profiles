package com.abi.profiles;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.content.BroadcastReceiver;
import android.util.Log;

import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.view.Window;

import android.widget.Button;
import android.widget.CompoundButton;
import android.app.Dialog;
import android.os.Parcelable;
import android.net.Uri;
import android.view.MenuItem;
import android.content.Context;
import android.content.IntentFilter;
import android.widget.CheckBox;

public class Profiles extends Activity implements OnClickListener, OnLongClickListener, OnCheckedChangeListener {
    public static final int INSERT_ID = Menu.FIRST;
    private static final String DEBUG_TAG = "QuickProfiles";
    private static SettingHandler mSettingHandler;
    private static SettingHandler mHandler;
    private static Button mProfile1;
    private static Button mProfile2;
    private static Button mProfile3;
    private static Button mProfile4;
    private static Button mProfile5;
    private static Button mProfile6;
    private static Button mProfile7;
    private static Button mProfile8;

    public static final String EXTRA_KEY = "com.abi.profiles.extra";
    
    public static final String WIDGET_INTENT = "com.abi.profiles.WidgetAction";
    public static final String WIDGET_EXTRA_KEY = "com.abi.profiles.WidgetExtra";

    private static final int HELP_DIALOG_ID = 1;
    public static final int NUMBER_OF_PROFILES = 8;
    private static int mRealPause = 0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Log.i(DEBUG_TAG, "Trying to set content view widget should work [Profiles]");
        setContentView(R.layout.profile_list);

        // Find out if we need to display the "Buy" button or not
        Intent checkIntent = new Intent();
        checkIntent.setAction(getPackageName()+".CheckLicense");
        sendBroadcast(checkIntent);
        Button buyButton = (Button) findViewById(R.id.buyLicense);
        buyButton.setVisibility(android.view.View.VISIBLE);
        buyButton.setOnClickListener(this);
        BroadcastReceiver showBuyButton = new BroadcastReceiver(){
            public void onReceive(Context cx, Intent intent){
                //Log.i(DEBUG_TAG, "Received broadcast [Profiles]");
                Button buyButtonInner = (Button) findViewById(R.id.buyLicense);
                buyButtonInner.setVisibility(android.view.View.GONE);
            }
        };
        IntentFilter showFilter = new IntentFilter(MyWidgetProvider.LICENSE_BROADCAST);
        registerReceiver(showBuyButton, showFilter);
        // Set up all our button listeners
        mProfile1 = (Button) findViewById(R.id.Profile1);
        mProfile2 = (Button) findViewById(R.id.Profile2);
        mProfile3 = (Button) findViewById(R.id.Profile3);
        mProfile4 = (Button) findViewById(R.id.Profile4);
        mProfile5 = (Button) findViewById(R.id.Profile5);
        mProfile6 = (Button) findViewById(R.id.Profile6);
        mProfile7 = (Button) findViewById(R.id.Profile7);
        mProfile8 = (Button) findViewById(R.id.Profile8);



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
        mProfile6.setOnClickListener(this);
        mProfile6.setOnLongClickListener(this);
        mProfile7.setOnClickListener(this);
        mProfile7.setOnLongClickListener(this);
        mProfile8.setOnClickListener(this);
        mProfile8.setOnLongClickListener(this);
        // done with that
        //Log.i(DEBUG_TAG, "Finished creating [Profiles]");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //TODO implement add profile
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.show_help_dialog);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //TODO implement add profile
        switch (item.getItemId()){
            case INSERT_ID:
                //addProfile();
                //Log.i(DEBUG_TAG, "Showing dialog from options [Profiles]");
                showDialog(HELP_DIALOG_ID);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        //Log.i(DEBUG_TAG, "Flags are "+getIntent().getFlags()+" [Profiles]");

        //Log.i(DEBUG_TAG, "Trying to resume [Profiles]");
        //Log.i(DEBUG_TAG, "calling activity is "+getCallingActivity()+" [ProfileList]");
        super.onResume();

        // set our button text values
        mHandler = new SettingHandler(this, 0);
        mHandler.mWindow = getWindow();
        setProfileNames();
        mHandler.setProf(0);
        String profString = mHandler.getSetting(SettingHandler.SettingsEnum.TITLE) ;
        Integer showGreetingDialog = Integer.valueOf(mHandler.getSetting(SettingHandler.SettingsEnum.SHOW_HELP));

        if (showGreetingDialog == 1) showDialog(HELP_DIALOG_ID);

        //Log.i(DEBUG_TAG, "Trying to set pressed "+profString+" [Profiles]");
        int profile = profString==null ? 0: Integer.valueOf(profString);

        mProfile1.setPressed(false);
        mProfile2.setPressed(false);
        mProfile3.setPressed(false);
        mProfile4.setPressed(false);
        mProfile5.setPressed(false);
        mProfile6.setPressed(false);
        mProfile7.setPressed(false);
        mProfile8.setPressed(false);
        switch (profile){
            case 1:
                mProfile1.setPressed(true);
                break;
            case 2:
                mProfile2.setPressed(true);
                break;
            case 3:
                mProfile3.setPressed(true);
                break;
            case 4:
                mProfile4.setPressed(true);
                break;
            case 5:
                mProfile5.setPressed(true);
                break;
            case 6:
                mProfile6.setPressed(true);
                break;
            case 7:
                mProfile7.setPressed(true);
                break;
            case 8:
                mProfile8.setPressed(true);
                break;
        }
    }

    //@Override
    //public void onPause(){
        //super.onPause();
        //if (mRealPause == 1){
            //mRealPause = 0;
        //}
        //else{
            //finish();
        //}
    //}
    //private void realPause(){
        //mRealPause = 1;
    /*}*/
    
    public Dialog onCreateDialog(int id){
        Dialog dialog = new Dialog(this);
        switch (id){
            case HELP_DIALOG_ID:
                //Log.i(DEBUG_TAG, "Create dialog called [Profiles]");
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.help_dialog);

                Button doneHelp = (Button) dialog.findViewById(R.id.ok_help);
                doneHelp.setOnClickListener(this);
                CheckBox showHelp = (CheckBox) dialog.findViewById(R.id.show_dialog_pref);
                showHelp.setOnCheckedChangeListener(this);
                break;
        }
        return dialog;
    }

    public void onPrepareDialog(int id, Dialog dg){
        SettingHandler handler = new SettingHandler(this,0);
        //Log.i(DEBUG_TAG, "Trying to prepare dialog "+id+" "+handler+" "+dg+" [Profiles]");
        switch (id){
            case HELP_DIALOG_ID:
                String showHelp = handler.getSetting(SettingHandler.SettingsEnum.SHOW_HELP);
                CheckBox helpBox = (CheckBox) dg.findViewById(R.id.show_dialog_pref);
                if (showHelp.equals("0")){
                    helpBox.setChecked(true);
                }
                else{
                    helpBox.setChecked(false);
                }
                break;
        }
    }

    public void onCheckedChanged (CompoundButton buttonview, boolean isChecked){
        mHandler.setProf(0);
        if (isChecked){
            mHandler.writeSetting(SettingHandler.SettingsEnum.SHOW_HELP, "0");
        }
        else{
            mHandler.writeSetting(SettingHandler.SettingsEnum.SHOW_HELP, "1");
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

    private void addProfile(){
        //TODO implement this
        mHandler.setProf(0);
        mHandler.addProfile();
    }

    private void updateUI(){
        //TODO implement this
    }

    public void onClick(View v){
        int profNum = -1;
        //Log.i(DEBUG_TAG, "OnClick called [Profiles]");
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
            case R.id.Profile6:
                profNum = 6;
                break;
            case R.id.Profile7:
                profNum = 7;
                break;
            case R.id.Profile8:
                profNum = 8;
                break;
            case R.id.buyLicense:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri data = new Uri.Builder().scheme(BlankActivity.MARKET_SCHEME).authority(BlankActivity.MARKET_SEARCH).appendQueryParameter(BlankActivity.MARKET_KEY,BlankActivity.MARKET_PACKAGE_NAME+BlankActivity.LICENSE_PACKAGE).build();
                intent.setData(data);
                //Log.i(DEBUG_TAG, "Performing search with "+data+" [BlankActivity]");
                startActivity(intent);
                break;
            case R.id.ok_help:
                removeDialog(HELP_DIALOG_ID);
                break;
        }
        if (profNum != -1){
            // first set our TITLE value, so we know which profile is set.
            // this corresponds to profile 0.
            mHandler.setProf(0);
            mHandler.writeSetting(SettingHandler.SettingsEnum.TITLE, String.valueOf(profNum));

            // now set all our setting values
            mHandler.setProf(profNum);
            mHandler.setProfile();

            finish();
        }
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
            case R.id.Profile6:
                i.putExtra(EXTRA_KEY,6);
                profNum = 6;
                break;
            case R.id.Profile7:
                i.putExtra(EXTRA_KEY,7);
                profNum = 7;
                break;
            case R.id.Profile8:
                i.putExtra(EXTRA_KEY,8);
                profNum = 8;
                break;
        }

        mHandler.writeSetting(SettingHandler.SettingsEnum.TITLE, String.valueOf(profNum));
        //Log.i(DEBUG_TAG, "Starting reg activity [Profiles]");
        startActivity(i);
        return true;
    }

}
