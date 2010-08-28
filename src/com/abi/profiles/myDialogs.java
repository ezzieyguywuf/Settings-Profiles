package com.abi.profiles;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Button;
import android.util.Log;
import android.widget.EditText;

class myDialogs {
    //private int mId;
    //private ProfileList mpList;
    private static final String DEBUG_TAG = "QuickProfiles";

    /*
     *myDialogs(int id, ProfileList cx){
     *    mId = id;
     *    mpList = cx;
     *}
     */

    public Dialog createDialog(ProfileList mpList, int mId) {
        Log.i(DEBUG_TAG, "creating dialog [myDialogs]");
        Dialog dialog = new Dialog(mpList);
        Log.i(DEBUG_TAG, "Our object is "+dialog+" [myDialogs]");
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        switch (mId){
            case ProfileList.VIBRATE_DIALOG_ID:
                dialog.setContentView(R.layout.vibrate_dialog);
                dialog.setCancelable(true);

                TextView silent = (TextView) dialog.findViewById(R.id.silent);
                TextView vibOnly = (TextView) dialog.findViewById(R.id.vibration_only);
                TextView sndOnly = (TextView) dialog.findViewById(R.id.sound_only);
                TextView sndAndVibrate = (TextView) dialog.findViewById(R.id.sound_and_vibration);

                silent.setOnClickListener(mpList);
                vibOnly.setOnClickListener(mpList);
                sndOnly.setOnClickListener(mpList);
                sndAndVibrate.setOnClickListener(mpList);

                break;
            case ProfileList.VOLUME_DIALOG_ID:
                dialog.setContentView(R.layout.volume_dialog);
                dialog.setCancelable(false);

                Log.i(DEBUG_TAG, "Creating the volume dialog [ProfileList]");
                // This is the listener for the check box
                CheckBox notifBind = (CheckBox) dialog.findViewById(R.id.notification_bind);
                int checked = Integer.valueOf(ProfileList.mHandler.getSetting(ProfileList.ENUM_NOTIFICATION_BIND));
                if (checked == 1){
                    notifBind.setChecked(true);
                }
                else {
                    notifBind.setChecked(false);
                }
                notifBind.setOnCheckedChangeListener(mpList);
                // This is for the notification_bind
                String notification_bind = ProfileList.mHandler.getSetting(ProfileList.ENUM_NOTIFICATION_BIND);
                // This is for volume control
                SeekBar ringerVolume = (SeekBar) dialog.findViewById(R.id.ringer_volume);
                SeekBar notificationVolume = (SeekBar) dialog.findViewById(R.id.notification_volume);
                if (notification_bind.equals("1")){
                    notificationVolume.setEnabled(false);
                }
                else {
                    notificationVolume.setEnabled(true);
                }
                SeekBar mediaVolume = (SeekBar) dialog.findViewById(R.id.media_volume);
                SeekBar alarmVolume = (SeekBar) dialog.findViewById(R.id.alarm_volume);
                SeekBar voiceCallVolume = (SeekBar) dialog.findViewById(R.id.voice_call_volume);
                SeekBar systemVolume = (SeekBar) dialog.findViewById(R.id.system_volume);

                // Set all the max values
                TextView ringText = (TextView) dialog.findViewById(R.id.ring_max);
                TextView notifText = (TextView) dialog.findViewById(R.id.notif_max);
                TextView mediaText = (TextView) dialog.findViewById(R.id.media_max);
                TextView alarmText = (TextView) dialog.findViewById(R.id.alarm_max);
                TextView voiceText = (TextView) dialog.findViewById(R.id.voice_max);
                TextView systemText = (TextView) dialog.findViewById(R.id.system_max);

                ringerVolume.setMax(ProfileList.mHandler.mAudioManager.getStreamMaxVolume(ProfileList.mHandler.mAudioManager.STREAM_RING));
                ringText.setText(String.valueOf(ProfileList.mHandler.mAudioManager.getStreamMaxVolume(ProfileList.mHandler.mAudioManager.STREAM_RING)));
                notificationVolume.setMax(ProfileList.mHandler.mAudioManager.getStreamMaxVolume(ProfileList.mHandler.mAudioManager.STREAM_NOTIFICATION));
                notifText.setText(String.valueOf(ProfileList.mHandler.mAudioManager.getStreamMaxVolume(ProfileList.mHandler.mAudioManager.STREAM_NOTIFICATION)));
                mediaVolume.setMax(ProfileList.mHandler.mAudioManager.getStreamMaxVolume(ProfileList.mHandler.mAudioManager.STREAM_MUSIC));
                mediaText.setText(String.valueOf(ProfileList.mHandler.mAudioManager.getStreamMaxVolume(ProfileList.mHandler.mAudioManager.STREAM_MUSIC)));
                alarmVolume.setMax(ProfileList.mHandler.mAudioManager.getStreamMaxVolume(ProfileList.mHandler.mAudioManager.STREAM_ALARM));
                alarmText.setText(String.valueOf(ProfileList.mHandler.mAudioManager.getStreamMaxVolume(ProfileList.mHandler.mAudioManager.STREAM_ALARM)));
                voiceCallVolume.setMax(ProfileList.mHandler.mAudioManager.getStreamMaxVolume(ProfileList.mHandler.mAudioManager.STREAM_VOICE_CALL));
                voiceText.setText(String.valueOf(ProfileList.mHandler.mAudioManager.getStreamMaxVolume(ProfileList.mHandler.mAudioManager.STREAM_VOICE_CALL)));
                systemVolume.setMax(ProfileList.mHandler.mAudioManager.getStreamMaxVolume(ProfileList.mHandler.mAudioManager.STREAM_SYSTEM));
                systemText.setText(String.valueOf(ProfileList.mHandler.mAudioManager.getStreamMaxVolume(ProfileList.mHandler.mAudioManager.STREAM_SYSTEM)));

                // set all slider values
                TextView ringVal = (TextView) dialog.findViewById(R.id.ring_value);
                TextView notifVal = (TextView) dialog.findViewById(R.id.notif_value);
                TextView mediaVal = (TextView) dialog.findViewById(R.id.media_value);
                TextView alarmVal = (TextView) dialog.findViewById(R.id.alarm_value);
                TextView voiceVal = (TextView) dialog.findViewById(R.id.voice_value);
                TextView systemVal = (TextView) dialog.findViewById(R.id.system_value);

                ringVal.setText(ProfileList.mHandler.getSetting(ProfileList.ENUM_RINGER_VOLUME));
                ringerVolume.setProgress(Integer.valueOf(ProfileList.mHandler.getSetting(ProfileList.ENUM_RINGER_VOLUME)));
                if (notification_bind.equals("1")){
                    //Log.i(DEBUG_TAG, "Notification is bound to ringer [ProfileList]");
                    notifVal.setText(ProfileList.mHandler.getSetting(ProfileList.ENUM_RINGER_VOLUME));
                    notificationVolume.setProgress(Integer.valueOf(ProfileList.mHandler.getSetting(ProfileList.ENUM_RINGER_VOLUME)));
                }
                else {
                    notifVal.setText(ProfileList.mHandler.getSetting(ProfileList.ENUM_RINGER_VOLUME));
                    notificationVolume.setProgress(Integer.valueOf(ProfileList.mHandler.getSetting(ProfileList.ENUM_RINGER_VOLUME)));
                }
                mediaVal.setText(ProfileList.mHandler.getSetting(ProfileList.ENUM_MEDIA_VOLUME));
                mediaVolume.setProgress(Integer.valueOf(ProfileList.mHandler.getSetting(ProfileList.ENUM_MEDIA_VOLUME)));
                alarmVal.setText(ProfileList.mHandler.getSetting(ProfileList.ENUM_ALARM_VOLUME));
                alarmVolume.setProgress(Integer.valueOf(ProfileList.mHandler.getSetting(ProfileList.ENUM_ALARM_VOLUME)));
                voiceVal.setText(ProfileList.mHandler.getSetting(ProfileList.ENUM_VOICE_VOLUME));
                voiceCallVolume.setProgress(Integer.valueOf(ProfileList.mHandler.getSetting(ProfileList.ENUM_VOICE_VOLUME)));
                systemVal.setText(ProfileList.mHandler.getSetting(ProfileList.ENUM_SYSTEM_VOLUME));
                systemVolume.setProgress(Integer.valueOf(ProfileList.mHandler.getSetting(ProfileList.ENUM_SYSTEM_VOLUME)));

                // start all the Seek listeners
                ringerVolume.setOnSeekBarChangeListener(mpList);
                notificationVolume.setOnSeekBarChangeListener(mpList);
                mediaVolume.setOnSeekBarChangeListener(mpList);
                alarmVolume.setOnSeekBarChangeListener(mpList);
                voiceCallVolume.setOnSeekBarChangeListener(mpList);
                systemVolume.setOnSeekBarChangeListener(mpList);

                // Now take care of the buttons
                Button doneButton = (Button) dialog.findViewById(R.id.volume_done);
                Button cancelButton = (Button) dialog.findViewById(R.id.volume_cancel);
                doneButton.setOnClickListener(mpList);
                cancelButton.setOnClickListener(mpList);

                break;
            case ProfileList.PROFILE_NAME_ID:
                dialog.setContentView(R.layout.name_dialog);
                Button done = (Button) dialog.findViewById(R.id.name_ok);
                done.setOnClickListener(mpList);
                break;
        }
        if (dialog == null){Log.e(DEBUG_TAG, "Returning a null dialog. This is wrong, FIXME [ProfileList]");}
        return dialog;
    }
}
