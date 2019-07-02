package me.kumar.bluetoothterminal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

/**
 * Created by hetpa on 1/2/2019.
 */

public class InterCeptCall extends BroadcastReceiver{

    ITelephony telephonyService;

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            final String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {

//                context.startService(new Intent(context, MyService.class));
                //Toast.makeText(context,Toast.LENGTH_SHORT).show();
//                context.startService(new Intent(context, MyService.class));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

//b.send("{'state':'str','data':"+"'"+msg+"'"+"}");

/*
*
* */