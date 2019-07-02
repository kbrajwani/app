package me.kumar.bluetoothterminal;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import me.aflak.bluetooth.Bluetooth;

import java.util.ArrayList;
import java.util.Locale;
import android.speech.RecognizerIntent;
import android.widget.ImageButton;
import android.widget.Toast;

public class Chat extends AppCompatActivity implements Bluetooth.CommunicationCallback {
    private String name;
    private Bluetooth b;
    private EditText message;
    private Button send;
    private TextView text;
    private ScrollView scrollView;
    private boolean registered=false;
    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private InterCeptCall2 interCeptCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
                b.send("'"+msg+"'");
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



    private void promptSpeechInput() {
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
                    txtSpeechInput.setText(result.get(0));
                    message.append(" "+result.get(0));
                    String msg = message.getText().toString();
                    message.setText("");
                    b.send("'"+msg+"'");
                    Display("You: "+msg);
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


            default:
                return super.onOptionsItemSelected(item);
        }
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
    }

    @Override
    public void onDisconnect(BluetoothDevice device, String message) {
        Display("Disconnected!");
        Display("Connecting again...");
        b.connectToDevice(device);
    }

    @Override
    public void onMessage(String message) {
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
