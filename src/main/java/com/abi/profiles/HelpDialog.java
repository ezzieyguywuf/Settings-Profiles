package com.abi.profiles;

import android.app.Dialog;
import android.app.Activity;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.view.Window;
import android.view.View;

public class HelpDialog extends Activity implements OnClickListener{
    private static final int HELP_DIALOG_ID = 1;
    private static final String DEBUG_TAG = "QuickProfiles";

    public void onResume(){
        super.onResume();
        //Log.i(DEBUG_TAG, "onResume in HelpDialog.java [HelpDialog]");
        showDialog(HELP_DIALOG_ID);
    }

    public Dialog onCreateDialog(int id){
        //Log.i(DEBUG_TAG, "onCreateDialog was called [ProfileList]");
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.help_dialog);
       
        Button done = (Button) findViewById(R.id.ok_help);
        //Log.i(DEBUG_TAG, "Setting click listener [HelpDialog]");
        done.setOnClickListener(this);
        return dialog;
    }

    public void onClick(View v){
        //Log.i(DEBUG_TAG, "Click recieved [HelpDialeg]");
        finish();
    }

}
