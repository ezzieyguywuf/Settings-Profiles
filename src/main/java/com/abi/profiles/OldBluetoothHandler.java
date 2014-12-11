package com.abi.profiles;

import android.app.Activity;
import android.util.Log;

import java.lang.reflect.Method;
import android.content.Context;

public class OldBluetoothHandler {
    private static Object mService;
    private static Method[] mMethods = new Method[3]; // [0] enable, [1] disable, [2] getBluetoothState

    public static final int BLUETOOTH_STATE_UNKNOWN = -1;
    public static final int BLUETOOTH_STATE_OFF = 0;
    public static final int BLUETOOTH_STATE_TURNING_ON = 1;
    public static final int BLUETOOTH_STATE_ON = 2;
    public static final int BLUETOOTH_STATE_TURNING_OFF = 3;

    public static final String BLUETOOTH_ACTION_STATE_CHANGED = "android.bluetooth.intent.action.BLUETOOTH_STATE_CHANGED";
    public static final String BLUETOOTH_EXTRA_STATE = "android.bluetooth.intent.BLUETOOTH_STATE";
    private static final String DEBUG_TAG = "QuickProfiles";

    /*
     *static {
     *    try {
     *        Class.forName("OldBluetoothHandler");
     *    }
     *    catch (Throwable ex) {
     *        throw new RuntimeException(ex);
     *    }
     *}
     *
     *public static void checkAvailable(){}
     */

    public OldBluetoothHandler(Context cx) throws Exception{

        mService = cx.getSystemService("bluetooth"); // bluetooth
        Method[] methods = mMethods;
        
        if (mService == null) throw new IllegalStateException("bluetooth service not found");
        Method method;
        
        // get enabled
        method = mService.getClass().getMethod("enable");
        if (method != null) method.setAccessible(true);
        methods[0] = method;
        
        // get disabled
        method = mService.getClass().getMethod("disable");
        if (method != null) method.setAccessible(true);
        methods[1] = method;

        method = mService.getClass().getMethod("getBluetoothState");
        if (method != null) method.setAccessible(true);
        methods[2] = method;
        
    
    }
    public void setEnabled(boolean enabled) {
        try {
            Method method = mMethods[enabled ? 0 : 1];
            method.invoke(mService);
            return;
        } catch (Exception e) {
            //Log.e(DEBUG_TAG, "cannot enable/disable bluetooth", e);
        }
        return;
    }
    
    public int getState() {
        try {
            Method method = mMethods[2];
            Integer state = (Integer) method.invoke(mService);
            return state.intValue();
        } catch (Exception e) {
            //Log.e(DEBUG_TAG, "cannot getBluetoothState", e);
        }
        return BLUETOOTH_STATE_UNKNOWN;
    }
}
