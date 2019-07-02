package me.kumar.bluetoothterminal;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.pulltorefresh.PullToRefresh;



import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.ActivityNotFoundException;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.TextView;


public class Select extends Activity implements PullToRefresh.OnRefreshListener {
    private Bluetooth bt;
    private ListView listView;
    private Button not_found;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private List<BluetoothDevice> paired;
    private PullToRefresh pull_to_refresh;
    private boolean registered=false;
    private ScheduledExecutorService scheduleTaskExecutor;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select);


//        startService(new Intent(Select.this, MyService.class));

//        Calendar cal1 = Calendar.getInstance();
//        TimeZone tz = TimeZone.getTimeZone("GMT");
//        cal1.setTimeZone(tz);
//        cal1.set(Calendar.HOUR_OF_DAY, 8); //midday
//        cal1.set(Calendar.MINUTE, 01);
//        cal1.set(Calendar.SECOND, 00);
//
//
//        Calendar cal2 = Calendar.getInstance();
//        cal2.setTimeZone(tz);
//        cal2.set(Calendar.HOUR_OF_DAY, 8);//8pm for example
//        cal2.set(Calendar.MINUTE, 02);
//        cal1.set(Calendar.SECOND, 00);
//
//
//        AlarmManager am = (AlarmManager)getApplicationContext().getSystemService  (Context.ALARM_SERVICE);
//        Intent intent = new Intent(getApplicationContext(), AlarmReciever.class);
//        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        am.setRepeating(AlarmManager.RTC_WAKEUP, cal1.getTimeInMillis(),5*1000, pi);
//

//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.HOUR_OF_DAY, 11);
//        cal.set(Calendar.MINUTE, 53);
//        cal.set(Calendar.SECOND, 0);
//
//        if (cal1.getTimeInMillis() < System.currentTimeMillis())
//            Toast.makeText(Select.this, "Its been 5 minutes", Toast.LENGTH_SHORT).show();

//        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
//
//        //Schedule a task to run every 5 seconds (or however long you want)
//        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                // Do stuff here!
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        // Do stuff to update UI here!
//
//                        Toast.makeText(Select.this, "Its been 5 minutes", Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//
//            }
//        }, 0, 5, TimeUnit.SECONDS);

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        registered=true;

        bt = new Bluetooth(this);
        bt.enableBluetooth();

        pull_to_refresh = (PullToRefresh)findViewById(R.id.pull_to_refresh);
        listView =  (ListView)findViewById(R.id.list);
        not_found =  (Button) findViewById(R.id.not_in_list);

        pull_to_refresh.setListView(listView);
        pull_to_refresh.setOnRefreshListener(this);
        pull_to_refresh.setSlide(500);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(Select.this, Chat.class);
                i.putExtra("pos", position);
                if(registered) {
                    unregisterReceiver(mReceiver);
                    registered=false;
                }
                startActivity(i);
                finish();
            }
        });

        not_found.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Select.this, Scan.class);
                startActivity(i);
            }
        });

        addDevicesToList();
    }






    @Override
    public void onRefresh() {
        List<String> names = new ArrayList<String>();
        for (BluetoothDevice d : bt.getPairedDevices()){
            names.add(d.getName());
        }

        String[] array = names.toArray(new String[names.size()]);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, array);

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.removeViews(0, listView.getCount());
                listView.setAdapter(adapter);
                paired = bt.getPairedDevices();
            }
        });
        pull_to_refresh.refreshComplete();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(registered) {
            unregisterReceiver(mReceiver);
            registered=false;
        }
    }

    private void addDevicesToList(){
        paired = bt.getPairedDevices();

        List<String> names = new ArrayList<>();
        for (BluetoothDevice d : paired){
            names.add(d.getName());
        }

        String[] array = names.toArray(new String[names.size()]);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, array);

        listView.setAdapter(adapter);

        not_found.setEnabled(true);
    }



    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listView.setEnabled(false);
                            }
                        });
                        Toast.makeText(Select.this, "Turn on bluetooth", Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addDevicesToList();
                                listView.setEnabled(true);
                            }
                        });
                        break;
                }
            }
        }
    };

}

