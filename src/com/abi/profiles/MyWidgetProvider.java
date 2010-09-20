package com.abi.profiles;

import android.appwidget.AppWidgetProvider;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

class MyWidgetProvider extends AppWidgetProvider{
    public void onReceive(Context context, Intent intent){
        Log.i("QuickProfiles", "Widget intent = "+intent);
        super.onReceive(context, intent);
    }
}
