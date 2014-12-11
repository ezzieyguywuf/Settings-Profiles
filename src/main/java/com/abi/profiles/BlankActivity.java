package com.abi.profiles;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import android.content.Intent;
import java.lang.Thread;
import java.lang.Exception;
import android.os.Handler;
import android.os.Message;
import android.view.View.OnClickListener;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.net.Uri;

public class BlankActivity extends Activity implements OnClickListener{
    private static final String DEBUG_TAG = "QuickProfiles";
    private static Dialog mDialog;
    public static final String LICENSE_PACKAGE = "com.abi.profileslicense";
    public static final String MARKET_SCHEME = "market";
    public static final String MARKET_SEARCH = "search";
    public static final String MARKET_KEY = "q";
    public static final String MARKET_PACKAGE_NAME = "pname:";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Log.i(DEBUG_TAG, "BlankActivity called [BlankActivity]");
        //Log.i(DEBUG_TAG, "Flags are "+getIntent().getFlags()+" [BlankActivity]");
        /*Activity parent = getParent();*/
        //Activity superParent = getParent();
        //if (parent instanceof Activity){
            //superParent = parent.getParent();
            //if (superParent instanceof Activity) superParent.finish();
            //parent.finish();
        /*}*/
        Intent intent = getIntent();
        if (intent.getAction().equals(LICENSE_PACKAGE)){
            //Log.i(DEBUG_TAG, "Looks like the license is not present boob [BlankActivity]");
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.no_widget_dialog);
            Button done = (Button) findViewById(R.id.widget_dialog_done);
            Button search = (Button) findViewById(R.id.widget_perform_search);
            done.setOnClickListener(this);
            search.setOnClickListener(this);
        }
        else {
            //Log.i(DEBUG_TAG, "Looks like the license is present boob[BlankActivity]");
            int profNum = Integer.valueOf(intent.getData().getQueryParameter(MyWidgetProvider.URI_KEY));
            SettingHandler handler = new SettingHandler(this, profNum);
            handler.mWindow = getWindow();
            handler.setProfile();
            
            RefreshHandler delayHandler = new RefreshHandler();
            delayHandler.sleep(1);
        }
    }

    class RefreshHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            BlankActivity.this.finish();
        }

        public void sleep(long delayMillis) {
          this.removeMessages(0);
          sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.widget_dialog_done:
                this.finish();
                break;
            case R.id.widget_perform_search:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri data = new Uri.Builder().scheme(MARKET_SCHEME).authority(MARKET_SEARCH).appendQueryParameter(MARKET_KEY,MARKET_PACKAGE_NAME+LICENSE_PACKAGE).build();
                //Log.i(DEBUG_TAG, "Performing search with "+data+" [BlankActivity]");
                intent.setData(data);
                startActivity(intent);
                break;
        }
    }
}
