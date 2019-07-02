package me.kumar.bluetoothterminal;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.os.Handler;
import android.os.IBinder;
import android.Manifest.permission;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.sac.speech.GoogleVoiceTypingDisabledException;
import com.sac.speech.Speech;
import com.sac.speech.SpeechDelegate;
import com.sac.speech.SpeechRecognitionNotAvailable;


import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import com.android.internal.telephony.ITelephony;

public class MyService extends Service implements SpeechDelegate, Speech.stopDueToDelay {

    public static SpeechDelegate delegate;
    ITelephony telephonyService;
    private InterCeptCall3 interCeptCall;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //TODO do something useful
        try {
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                ((AudioManager) Objects.requireNonNull(
                        getSystemService(Context.AUDIO_SERVICE))).setStreamMute(AudioManager.STREAM_SYSTEM, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Speech.init(this);
        delegate = this;
        Speech.getInstance().setListener(this);


            System.setProperty("rx.unsafe-disable", "True");
            Toast.makeText(getApplicationContext(),"start",Toast.LENGTH_SHORT).show();
            // Always true pre-M
            try {
                Speech.getInstance().stopTextToSpeech();
                Speech.getInstance().startListening(null, this);
            } catch (SpeechRecognitionNotAvailable exc) {
                //showSpeechNotSupportedDialog();

            } catch (GoogleVoiceTypingDisabledException exc) {
                //showEnableGoogleVoiceTyping();
            }


            muteBeepSoundOfRecorder();


        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    @Override
    public void onStartOfSpeech() {
    }

    @Override
    public void onSpeechRmsChanged(float value) {

    }

    @Override
    public void onSpeechPartialResults(List<String> results) {
        for (String partial : results) {
            Log.d("Result", partial+"");
        }
    }

    @Override
    public void onSpeechResult(String result) {
        Log.d("Result", result+"");
        if (!TextUtils.isEmpty(result)) {

            Identify(result);

            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        }
    }

    public void Identify(String s){

        String arr[] = s.split(" ");
        if (Arrays.asList(arr).contains("reject") && Arrays.asList(arr).contains("call")){
            Toast.makeText(this,"reject" , Toast.LENGTH_SHORT).show();
            telephonyService.endCall();

        }
        else if (Arrays.asList(arr).contains("tell") && Arrays.asList(arr).contains("talk") && Arrays.asList(arr).contains("later")){
            Toast.makeText(this,"tell to talk later" , Toast.LENGTH_SHORT).show();

        }
        else if (Arrays.asList(arr).contains("tell") && Arrays.asList(arr).contains("call") && Arrays.asList(arr).contains("later")){
            Toast.makeText(this,"tell to call later" , Toast.LENGTH_SHORT).show();

        }
        else if (Arrays.asList(arr).contains("tell") && Arrays.asList(arr).contains("busy") && Arrays.asList(arr).contains("now")){
            Toast.makeText(this,"tell busy right now" , Toast.LENGTH_SHORT).show();

        }
        else if (Arrays.asList(arr).contains("tell") && Arrays.asList(arr).contains("I'll") && Arrays.asList(arr).contains("call")){
            Toast.makeText(this,"tell I'll call" , Toast.LENGTH_SHORT).show();
        }
        else if (Arrays.asList(arr).contains("tell") && Arrays.asList(arr).contains("I") && Arrays.asList(arr).contains("will") && Arrays.asList(arr).contains("call")){
            Toast.makeText(this,"tell I will call" , Toast.LENGTH_SHORT).show();

        }
        else if (Arrays.asList(arr).contains("reject")){
            Toast.makeText(this,"reject call" , Toast.LENGTH_SHORT).show();
            telephonyService.endCall();

        }
        else if (Arrays.asList(arr).contains("talk") && Arrays.asList(arr).contains("later")){
            Toast.makeText(this,"talk later" , Toast.LENGTH_SHORT).show();

        }
        else if (Arrays.asList(arr).contains("cut") && Arrays.asList(arr).contains("call")){
            Toast.makeText(this,"call cut" , Toast.LENGTH_SHORT).show();
            telephonyService.endCall();

        }
        else if (Arrays.asList(arr).contains("call") && Arrays.asList(arr).contains("later")){
            Toast.makeText(this,"call later" , Toast.LENGTH_SHORT).show();
            telephonyService.silenceRinger();
        }
        else if (Arrays.asList(arr).contains("later")){
            Toast.makeText(this,"later" , Toast.LENGTH_SHORT).show();
            telephonyService.silenceRinger();
        }
        else if (Arrays.asList(arr).contains("not") && Arrays.asList(arr).contains("right") && Arrays.asList(arr).contains("now")){
            Toast.makeText(this,"not right now" , Toast.LENGTH_SHORT).show();
            telephonyService.silenceRinger();
        }
        else if (Arrays.asList(arr).contains("not") && Arrays.asList(arr).contains("now")){
            Toast.makeText(this,"not now" , Toast.LENGTH_SHORT).show();
            telephonyService.silenceRinger();
        }
        else if (Arrays.asList(arr).contains("busy") && Arrays.asList(arr).contains("right") && Arrays.asList(arr).contains("now")){
            Toast.makeText(this,"busy right now" , Toast.LENGTH_SHORT).show();
            telephonyService.silenceRinger();
        }
        else if (Arrays.asList(arr).contains("busy") && Arrays.asList(arr).contains("now")){
            Toast.makeText(this,"busy now" , Toast.LENGTH_SHORT).show();
            telephonyService.silenceRinger();
        }
        else if (Arrays.asList(arr).contains("don't") && Arrays.asList(arr).contains("want") && Arrays.asList(arr).contains("talk")){
            Toast.makeText(this,"dont want to talk" , Toast.LENGTH_SHORT).show();
            telephonyService.silenceRinger();
        }
        else if (Arrays.asList(arr).contains("don't") && Arrays.asList(arr).contains("want") && Arrays.asList(arr).contains("answer")){
            Toast.makeText(this,"dont want to answer" , Toast.LENGTH_SHORT).show();
            telephonyService.endCall();

        }
        else if (Arrays.asList(arr).contains("answer") && Arrays.asList(arr).contains("it")){
            Log.d("answer","my answer");
            Toast.makeText(this,"answer call" , Toast.LENGTH_SHORT).show();
            telephonyService.answerRingingCall();
            Log.d("answer","my answer");
        }
        else if (Arrays.asList(arr).contains("answer") && Arrays.asList(arr).contains("call")){
            Log.d("answer","my answer");
            Toast.makeText(this,"answer it" , Toast.LENGTH_SHORT).show();
            telephonyService.answerRingingCall();
            Log.d("answer","my answer");
        }
        else if (Arrays.asList(arr).contains("pick") && Arrays.asList(arr).contains("up")){
            Toast.makeText(this,"pick up" , Toast.LENGTH_SHORT).show();
            telephonyService.answerRingingCall();
        }
        else if (Arrays.asList(arr).contains("answer")){
            Log.d("answer","my answer");
            Toast.makeText(this,"answer" , Toast.LENGTH_SHORT).show();
            telephonyService.answerRingingCall();
            Log.d("answer","my answer");
        }
        else if (Arrays.asList(arr).contains("silent")){
            Toast.makeText(this,"silent" , Toast.LENGTH_SHORT).show();
            telephonyService.silenceRinger();
        }
        else if (Arrays.asList(arr).contains("silence")){
            Toast.makeText(this,"silence" , Toast.LENGTH_SHORT).show();
            telephonyService.silenceRinger();
        }
        else if (Arrays.asList(arr).contains("mute")){
            Toast.makeText(this,"mute" , Toast.LENGTH_SHORT).show();
            telephonyService.silenceRinger();
        }
    }



    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(getApplicationContext(),"service started",Toast.LENGTH_SHORT).show();

        interCeptCall = new InterCeptCall3();
        registerReceiver(interCeptCall,new IntentFilter("android.intent.action.PHONE_STATE"));
    }

    @Override
    public void onSpecifiedCommandPronounced(String event) {
        try {


            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                ((AudioManager) Objects.requireNonNull(
                        getSystemService(Context.AUDIO_SERVICE))).setStreamMute(AudioManager.STREAM_SYSTEM, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Speech.getInstance().isListening()) {
            muteBeepSoundOfRecorder();
            Speech.getInstance().stopListening();
        } else {
                    try {
                        Speech.getInstance().stopTextToSpeech();
                        Speech.getInstance().startListening(null, this);
                    } catch (SpeechRecognitionNotAvailable exc) {
                        //showSpeechNotSupportedDialog();

                    } catch (GoogleVoiceTypingDisabledException exc) {
                        //showEnableGoogleVoiceTyping();
                    }

            muteBeepSoundOfRecorder();
        }
    }

    /**
     * Function to remove the beep sound of voice recognizer.
     */
    private void muteBeepSoundOfRecorder() {
        AudioManager amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (amanager != null) {
//            amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);

//            amanager.setStreamMute(AudioManager.STREAM_ALARM, true);
            amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
//            amanager.setStreamMute(AudioManager.STREAM_RING, true);
            amanager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //Restarting the service if it is removed.
        PendingIntent service =
                PendingIntent.getService(getApplicationContext(), new Random().nextInt(),
                        new Intent(getApplicationContext(), MyService.class), PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
        super.onTaskRemoved(rootIntent);
    }

    public class InterCeptCall3 extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            try {



                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                final String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)){
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    try {
                        Method m = tm.getClass().getDeclaredMethod("getITelephony");

                        m.setAccessible(true);
                        telephonyService = (ITelephony) m.invoke(tm);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)) {
                    Toast.makeText(getApplicationContext(),"service stoped",Toast.LENGTH_SHORT).show();
                    context.stopService(new Intent(context, MyService.class));
                }
                if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK))
                {
                    Toast.makeText(getApplicationContext(),"call answerd",Toast.LENGTH_SHORT).show();
                    // try and turn on speaker phone
                    final Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

                            // this doesnt work without android.permission.MODIFY_PHONE_STATE
                            // audioManager.setMode(AudioManager.MODE_IN_CALL);

                            // weirdly this works
                            audioManager.setMode(AudioManager.MODE_NORMAL); // this is important
                            audioManager.setSpeakerphoneOn(true);

                            // note the phone interface won't show speaker phone is enabled
                            // but the phone speaker will be on
                            // remember to turn it back off when your done ;)
                        }
                    }, 500); // half a second delay is important or it might fail
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }


}