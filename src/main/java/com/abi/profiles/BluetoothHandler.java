package com.abi.profiles;

import android.bluetooth.BluetoothAdapter;
import android.util.Log;

public class BluetoothHandler {
    public BluetoothAdapter mBtAdapter;
    public static final int STATE_TURNING_ON = 0 ;
    public static final int STATE_ON = 1 ;
    public static final int STATE_TURNING_OFF = 2;
    public static final int STATE_OFF = 3 ;

    private static final String DEBUG_TAG= "QuickProfiles";
    static {
        try {
            //Log.i(DEBUG_TAG, "Trying to instantiate... ");
            Class.forName("android.bluetooth.BluetoothAdapter");
        }
        catch (Exception ex) {
            //Log.i(DEBUG_TAG, "There is no bluetooth adapter available");
            throw new RuntimeException(ex);
        }
    }

    /* calling here forces class initialization */
    public static void checkAvailable() {}


    public BluetoothHandler(){
        //Log.i(DEBUG_TAG, "Ok, trying to get an adapter [BluetoothHandler]");
        mBtAdapter = (BluetoothAdapter) BluetoothAdapter.getDefaultAdapter();
        //if (mBtAdapter == null) Log.i(DEBUG_TAG, "fail [BluetoothHandler]");
        //else Log.i(DEBUG_TAG, "success [BluetoothHandler]");
    }

    public boolean disable(){
        //Log.i(DEBUG_TAG, "Trying to disable [BluetoothHandler]");
        return mBtAdapter.disable();
    }

    public boolean enable(){
        return mBtAdapter.enable();
    }
    
    public int getState(){
        int state = mBtAdapter.getState();
        switch (state){
            case BluetoothAdapter.STATE_TURNING_ON:
                return STATE_TURNING_ON;
            case BluetoothAdapter.STATE_ON:
                return STATE_ON;
            case BluetoothAdapter.STATE_TURNING_OFF:
                return STATE_TURNING_OFF;
            case BluetoothAdapter.STATE_OFF:
                return STATE_OFF;
        }
        return -1;
    }
}
