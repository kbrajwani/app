package me.kumar.bluetoothterminal;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import java.lang.reflect.Method;
import com.android.internal.telephony.ITelephony;

import me.aflak.bluetooth.Bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import android.speech.RecognizerIntent;
import android.widget.ImageButton;
import android.widget.Toast;

public class Chat extends AppCompatActivity implements Bluetooth.CommunicationCallback{
    private String name;

    static private Bluetooth b;
    private EditText message;
    private Button send;
    private TextView text;
    private ScrollView scrollView;
    private boolean registered=false;
    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private InterCeptCall2 interCeptCall;
    BluetoothSocket Socket1;
    InputStream inStream = null;
    ITelephony telephonyService;
    static LocationManager locationManager;
    AlarmManager am;
    PendingIntent pi;

    static private boolean isBConected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        am = (AlarmManager)getApplicationContext().getSystemService  (Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReciever2.class);
        pi = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 30);
// With setInexactRepeating(), you have to use one of the AlarmManager interval
// constants--in this case, AlarmManager.INTERVAL_DAY.


        Context context = this;
        Intent background = new Intent(context, LocationService.class);
        context.startService(background);
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

        // hide the action bar
//        getActionBar().hide();

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        text = (TextView)findViewById(R.id.text);
        message = (EditText)findViewById(R.id.message);
        send = (Button)findViewById(R.id.send);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        text.setMovementMethod(new ScrollingMovementMethod());
        send.setEnabled(false);
        btnSpeak.setEnabled(false);

        b = new Bluetooth(this);
        b.enableBluetooth();

        b.setCommunicationCallback(this);

        int pos = getIntent().getExtras().getInt("pos");
        name = b.getPairedDevices().get(pos).getName();

        Display("Connecting...");
        b.connectToDevice(b.getPairedDevices().get(pos));

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = message.getText().toString();
                message.setText("");
                b.send("{'state':'str','data':"+"'"+msg+"'"+"}");
//                b.send("'"+msg+"'");
                Display("You: "+msg);
            }
        });

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        registered=true;

        Toast.makeText(Chat.this,"chat",Toast.LENGTH_SHORT).show();

        if(ContextCompat.checkSelfPermission(Chat.this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(Chat.this,
                    Manifest.permission.READ_PHONE_STATE)){
                ActivityCompat.requestPermissions(Chat.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},1);
            }
            else {
                ActivityCompat.requestPermissions(Chat.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},1);
            }
        }
        else {

        }
        if(ContextCompat.checkSelfPermission(Chat.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(Chat.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(Chat.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            else {
                ActivityCompat.requestPermissions(Chat.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }
        else {

        }
        if(ContextCompat.checkSelfPermission(Chat.this,
                Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(Chat.this,
                    Manifest.permission.READ_SMS)){
                ActivityCompat.requestPermissions(Chat.this,
                        new String[]{Manifest.permission.READ_SMS},1);
            }
            else {
                ActivityCompat.requestPermissions(Chat.this,
                        new String[]{Manifest.permission.READ_SMS},1);
            }
        }
        else {

        }
        if(ContextCompat.checkSelfPermission(Chat.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(Chat.this,
                    Manifest.permission.CALL_PHONE)){
                ActivityCompat.requestPermissions(Chat.this,
                        new String[]{Manifest.permission.CALL_PHONE},1);
            }
            else {
                ActivityCompat.requestPermissions(Chat.this,
                        new String[]{Manifest.permission.CALL_PHONE},1);
            }
        }
        else {

        }
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("android.intent.action.PHONE_STATE");
        interCeptCall = new InterCeptCall2();
//        registerReceiver(interCeptCall, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(interCeptCall,new IntentFilter("android.intent.action.PHONE_STATE"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:{
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(Chat.this,
                            Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this,"permission granted",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(this,"permission not granted",Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
            }
        }
    }



    public void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if(isBConected) {
                        txtSpeechInput.setText(result.get(0));
                        message.append(" " + result.get(0));
                        String msg = message.getText().toString();
                        message.setText("");
                        b.send("{'state':'str','data':" + "'" + msg + "'" + "}");
//                    b.send("'"+msg+"'");
                        Display("You: " + msg);

                    }
                    telephonyService.endCall();

                }
                break;
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(registered) {
            unregisterReceiver(mReceiver);
//            unregisterReceiver(interCeptCall);
            registered=false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!isBConected) {
            menu.getItem(1).setEnabled(false);
        }else
        {
            menu.getItem(1).setEnabled(true);
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (30*1000),
                    30 * 1000, pi);
        }
         return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.close:
                b.removeCommunicationCallback();
                b.disconnect();
                Intent intent = new Intent(this, Select.class);
                startActivity(intent);
                finish();
                return true;

            case R.id.sync:
                SyncContacts();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void SyncContacts(){
        ArrayList<String> numbers = new ArrayList<>();
        int counter =0;
        String number = new String();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNumber = phoneNumber.replaceAll("\\s", "");
            if (!numbers.contains(name)) {
                counter = counter + 1;
                number=number+"\""+name.replace("'","")+"\"";
//                number=number+name.replace("'","");
                number=number+",";
                number=number+"\""+phoneNumber.replace("(","").replace(")","").replace("-","")+"\"";
//                number=number+phoneNumber;
                number=number+",";
                numbers.add(name);
                numbers.add(phoneNumber);
                if (counter % 20 == 0) {
                    try {
                        number = number.substring(0, number.length() - 1);
                        b.send("{'state':4,'contacts':'" + number + "'}");
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    counter = 0;
                    number = "";
                }
            }
        }
        for (String s : numbers){
            Log.e("My array list content: ", s);
        }
        phones.close();
    }

    public void Display(final String s){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.append(s + "\n");
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    @Override
    public void onConnect(BluetoothDevice device) {
        Display("Connected to "+device.getName()+" - "+device.getAddress());
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                send.setEnabled(true);
            }
        });
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnSpeak.setEnabled(true);
            }
        });
        try {
            isBConected = true;
            BluetoothSocket Socket1  = b.getSocket();
            inStream = Socket1.getInputStream();

            String v = read(); //Get version from Edison
            Display("Version: "+v);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//        invalidateOptionsMenu();
//        b.send("{'state':4,'contacts':'"+number+"'}");
    }

    public String read() throws IOException {
        byte[] buffer = new byte[64];
        inStream.read(buffer);
        String s = new String(buffer);
        return s;
    }

    @Override
    public void onDisconnect(BluetoothDevice device, String message) {
        Display("Disconnected!");
        Display("Connecting again...");
        b.connectToDevice(device);
        isBConected = false;
//        invalidateOptionsMenu();
    }

    @Override
    public void onMessage(String message)
    {
        Log.d("msg: ", message);
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        Display(name+": "+message);
    }

    @Override
    public void onError(String message) {
        Display("Error: "+message);
    }

    @Override
    public void onConnectError(final BluetoothDevice device, String message) {
        Display("Error: "+message);
        Display("Trying again in 3 sec.");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        b.connectToDevice(device);
                    }
                }, 2000);
            }
        });
    }

    public static class AlarmReciever2 extends BroadcastReceiver
    {
        @Override
        public void onReceive(final Context context, Intent intent)
        {
            // TODO Auto-generated method stub

            LocationListener locationListenerGPS=new LocationListener() {
                @Override
                public void onLocationChanged(android.location.Location location) {
                    double latitude=location.getLatitude();
                    double longitude=location.getLongitude();
                    String msg=latitude + ","+longitude;
                    Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
                    if(isBConected) {
                        b.send("{'state':5,'loc':" + "'" + msg + "'" + "}");
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            locationManager=(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER,
                    2000,
                    10, locationListenerGPS);
            // here you can start an activity or service depending on your need
            // for ex you can start an activity to vibrate phone or to ring the phone

            // Show the toast  like in above screen shot
            Toast.makeText(context, "Alarm Triggered and SMS Sent", Toast.LENGTH_LONG).show();
        }

    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                Intent intent1 = new Intent(Chat.this, Select.class);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        if(registered) {
                            unregisterReceiver(mReceiver);
                            registered=false;
                        }
                        startActivity(intent1);

                        finish();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        if(registered) {
                            unregisterReceiver(mReceiver);
                            registered=false;
                        }
                        startActivity(intent1);
                        finish();
                        break;
                }
            }
        }
    };

    public class InterCeptCall2 extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                message.requestFocus();
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

                switch (state){
                    case "RINGING":
                        Toast.makeText(context,state,Toast.LENGTH_SHORT).show();
                        Toast.makeText(context,"111+",Toast.LENGTH_SHORT).show();
                        b.send("{'state':1,'number':"+"'"+incomingNumber+"'"+"}");

                        promptSpeechInput();
                        break;
                    case "IDLE":
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                b.send("{'state':2,'number':"+"'"+incomingNumber+"'"+"}");
                            }
                        }, 1000);

                        break;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}