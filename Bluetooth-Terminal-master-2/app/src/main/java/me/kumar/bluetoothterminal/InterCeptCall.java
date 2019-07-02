package me.kumar.bluetoothterminal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Created by hetpa on 1/2/2019.
 */

public class InterCeptCall extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Toast.makeText(context,"ringing from"+incomingNumber,Toast.LENGTH_SHORT).show();
            //Toast.makeText(context,Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
