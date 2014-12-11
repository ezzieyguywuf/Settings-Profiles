package com.abi.profiles;

import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetManager;
import android.widget.RemoteViews;
import android.widget.ToggleButton;
import android.view.View.OnClickListener;
import android.content.Context;
import android.view.View;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import android.net.Uri;
import android.app.Activity;
import android.widget.Button;
import android.os.Bundle;
import android.content.ComponentName;

public class MyWidgetProvider extends AppWidgetProvider {
    private static final String DEBUG_TAG = "QuickProfiles";

    public static final String URI_SCHEME = "widget";
    public static final String URI_HOST   = "com.abi.profiles";
    public static final String URI_KEY    = "widgetButtonNumber";
    public static final String WIDGET_BROADCAST = "com.abi.profiles.WidgetBroadcast";
    public static final String LICENSE_BROADCAST = "com.abi.profiles.LicenseBroadcast";
    private static AppWidgetManager mAppWidgetManager;
    private static int licenseExists = -1;
    private static int[] mAppWidgetIds = {1};

    public void onUpdate (Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        final int N = appWidgetIds.length;
        //Log.i(DEBUG_TAG, "onUpdate called");
        // Do this for each instance of our widget.
        for (int i=0; i<N; i++){
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
    /**
     * Creates PendingIntent to notify the widget of a button click.
     *
     * @param context
     * @param appWidgetId
     * @return
     */
    // Stolen from android_source/packages/apps/Settings/src/com/android/settings/widget/SettingsAppWidgetProvider.java
    private static PendingIntent getLaunchPendingIntent(Context context, int buttonId) {
        Uri baseUri = new Uri.Builder().scheme(URI_SCHEME).authority(URI_HOST).appendQueryParameter(URI_KEY, String.valueOf(buttonId)).build();
        Intent launchIntent = new Intent();
        //launchIntent.setClass(context, MyWidgetProvider.class);
        launchIntent.setClass(context, BlankActivity.class);
        launchIntent.setAction(WIDGET_BROADCAST);
        launchIntent.setData(baseUri);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //launchIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        //Log.i(DEBUG_TAG, "added flag "+Intent.FLAG_ACTIVITY_NEW_TASK+" [MyWidgetProvider]");
        //PendingIntent pi = PendingIntent.getBroadcast(context, 0 [> no requestCode */, launchIntent, 0 /* no flags <]);
        PendingIntent pi = PendingIntent.getActivity(context, 0 /* no requestCode */, launchIntent, 0 /* no flags */);
        return pi;
    }

    private static PendingIntent getDialogPendingIntent(Context context){
        Intent dialogIntent = new Intent();
        dialogIntent.setClass(context, BlankActivity.class);
        dialogIntent.setAction(BlankActivity.LICENSE_PACKAGE);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //dialogIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        //Log.i(DEBUG_TAG, "added flag "+Intent.FLAG_ACTIVITY_NEW_TASK+" [MyWidgetProvider]");
        PendingIntent pi = PendingIntent.getActivity(context, 0, dialogIntent, 0);
        return pi;
    }

    private void setRealButtons(Context context){
        //Log.i(DEBUG_TAG, "setRealButtons calld [MyAppWidge]");
        ComponentName thisReceiver = new ComponentName("com.abi.profiles", "com.abi.profiles.MyWidgetProvider");
        mAppWidgetManager = AppWidgetManager.getInstance(context);
        mAppWidgetIds = mAppWidgetManager.getAppWidgetIds(thisReceiver);
        //Log.i(DEBUG_TAG, "Length of ids is "+mAppWidgetIds.length+" [MyWidgetProvider]");
        for (int i=0; i<mAppWidgetIds.length; i++){
            //Log.i(DEBUG_TAG, "In the for loop [MyAppWidget]");
            //Log.i(DEBUG_TAG, "appWidgetIds are "+mAppWidgetIds[i]+" [MyWidgetProvider]");
            int appWidgetId = mAppWidgetIds[i];

            RemoteViews views = setText(context);
            views.setOnClickPendingIntent(R.id.prof1, getLaunchPendingIntent(context,1));
            views.setOnClickPendingIntent(R.id.prof2, getLaunchPendingIntent(context,2));
            views.setOnClickPendingIntent(R.id.prof3, getLaunchPendingIntent(context,3));
            views.setOnClickPendingIntent(R.id.prof4, getLaunchPendingIntent(context,4));
            views.setOnClickPendingIntent(R.id.prof5, getLaunchPendingIntent(context,5));
            mAppWidgetManager.updateAppWidget(mAppWidgetIds[i], views);
            //Log.i(DEBUG_TAG, "set real buttons [MyWidgetProvider]");
        }
    }

    private void setBuyDialog(Context context){
        //Log.i(DEBUG_TAG, "setBuyDialogs called [MyAppWidget]");
        ComponentName thisReceiver = new ComponentName("com.abi.profiles", "com.abi.profiles.MyWidgetProvider");
        mAppWidgetManager = AppWidgetManager.getInstance(context);
        mAppWidgetIds = mAppWidgetManager.getAppWidgetIds(thisReceiver);
        //Log.i(DEBUG_TAG, "Length of ids is "+mAppWidgetIds.length+" [MyWidgetProvider]");
        for (int i=0; i<mAppWidgetIds.length; i++){
            //Log.i(DEBUG_TAG, "In the for loop [MyAppWidget]");
            //Log.i(DEBUG_TAG, "appWidgetIds are "+mAppWidgetIds[i]+" [MyWidgetProvider]");
            int appWidgetId = mAppWidgetIds[i];

            RemoteViews views = setText(context);
            views.setOnClickPendingIntent(R.id.prof1, getDialogPendingIntent(context));
            //views.setInt(R.id.prof1, "setBackgroundColor", android.graphics.Color.YELLOW);
            views.setOnClickPendingIntent(R.id.prof2, getDialogPendingIntent(context));
            views.setOnClickPendingIntent(R.id.prof3, getDialogPendingIntent(context));
            views.setOnClickPendingIntent(R.id.prof4, getDialogPendingIntent(context));
            views.setOnClickPendingIntent(R.id.prof5, getDialogPendingIntent(context));
            mAppWidgetManager.updateAppWidget(mAppWidgetIds[i], views);
            //Log.i(DEBUG_TAG, "set fake buttons [MyWidgetProvider]");
       }
    }

    public static RemoteViews setText(Context context){
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        SettingHandler mHandler = new SettingHandler(context, 0);

        // this code sets the button titles
        for (int profile =1; profile <= Profiles.NUMBER_OF_PROFILES; profile++){
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
                        views.setTextViewText(R.id.prof1, text);
                        break;
                    case 2:
                        views.setTextViewText(R.id.prof2, text);
                        break;
                    case 3:
                        views.setTextViewText(R.id.prof3, text);
                        break;
                    case 4:
                        views.setTextViewText(R.id.prof4, text);
                        break;
                    case 5:
                        views.setTextViewText(R.id.prof5, text);
                        break;
                }
            }
        }
 
        return views;
    }
    

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.i(DEBUG_TAG, "Intent is "+intent+" [MyWidgetProvider]");

        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_ENABLED)){
            setBuyDialog(context);
            //Log.i(DEBUG_TAG, "Checking if licencse exists [MyWidgetProvider]");
            // Check if the license has been purchased and installed.
            licenseExists = 0;
            Intent checkIntent = new Intent();
            checkIntent.setAction(context.getPackageName()+".CheckLicense");
            context.sendBroadcast(checkIntent);
        }
        else if (intent.getAction().equals(LICENSE_BROADCAST)){
            //Log.i(DEBUG_TAG, "Received broadcast from license [MyWidgetProvider]");
            licenseExists=1;
            setRealButtons(context);
        }
        else if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)){
            if (licenseExists == 1){
                setRealButtons(context);
            }
            else{
                setBuyDialog(context);
            }
        }
        else if (intent.getAction().equals(android.content.Intent.ACTION_PACKAGE_ADDED)){
            if (intent.getData().getSchemeSpecificPart().equals("com.abi.profileslicense")){
                Intent checkIntent = new Intent();
                checkIntent.setAction(context.getPackageName()+".CheckLicense");
                context.sendBroadcast(checkIntent);
            }
        }
        else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)){
            if (intent.getData().getSchemeSpecificPart().equals("com.abi.profileslicense")){
                //Log.i(DEBUG_TAG, "License was removed [MyWidgetProvider]");
                licenseExists = 0;
                setBuyDialog(context);
            }
            //Bundle extras = intent.getExtras();
            //Uri data = intent.getData();
            //Log.i(DEBUG_TAG, "The extras bundle is "+extras+" [MyWidgetProvider]");
            //Log.i(DEBUG_TAG, "The data is "+data+" [MyWidgetProvider]");
            //String auth = (String) data.getSchemeSpecificPart();
            //Log.i(DEBUG_TAG, "The authority is "+auth+" [MyWidgetProvider]");
        }
    }
}
