package com.abi.profiles;

import android.content.Context;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class LicenseChecker{
    private static final String APP_TO_CHECK = "com.abi.profileslicense";
    private static final String DEBUG_TAG = "QuickProfiles";
    private static Context mCx;
    private static LicenseChecker mChecker;
    public static int exists = -1;

    private LicenseChecker(Context context){
        mCx = context;
    }

    public static LicenseChecker getLicenseChecker(Context context){
        if (mChecker == null){
            mChecker = new LicenseChecker(context);
        }
        return mChecker;
    }

    public int checkLicense(){
        PackageManager packageManager = mCx.getPackageManager();
        //Log.i(DEBUG_TAG, "Checking if license is installed");
        try{
            ApplicationInfo info = mCx.getPackageManager().getApplicationInfo(APP_TO_CHECK, 0);
            exists = 1;
        }
        catch (PackageManager.NameNotFoundException e){
            exists = 0;
        }
        return exists;
    }
}
