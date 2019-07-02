package me.kumar.bluetoothterminal;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.graphics.PixelFormat;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.Toast;

/*
public class Notifier extends AccessibilityService {

    WindowManager windowManager;
    ImageView back,home,notification,minimize;
    WindowManager.LayoutParams params;
    AccessibilityService service;

    @SuppressLint("RtlHardcoded")
    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);



        params= new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER_VERTICAL|Gravity.RIGHT;
        params.x = 10;
        params.y = 50;

        windowManager.addView(home, params);

        params= new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER_VERTICAL|Gravity.RIGHT;
        params.x = 10;
        params.y = 100;

        windowManager.addView(minimize, params);

        params= new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER_VERTICAL|Gravity.RIGHT;
        params.x = 10;
        params.y = 150;

        windowManager.addView(notification, params);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        minimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d("TAG", "onServiceConnected");
    }

}*/

public class Notifier extends AccessibilityService{

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "oncreate: ", Toast.LENGTH_SHORT).show();
    }

    protected void onServiceConnected() {

        Toast.makeText(this, "connected: ", Toast.LENGTH_SHORT).show();
        Log.d("Tortuga", "AccessibilityService Connected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        info.notificationTimeout = 100;
        setServiceInfo(info);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent e) {
        Log.d("Tortuga","FML");
        if (e.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            Log.d("Tortuga","Recieved event");
            Parcelable data = e.getParcelableData();
            if (data instanceof Notification) {
                Log.d("Tortuga","Recieved notification");
                Notification notification = (Notification) data;
                Toast.makeText(this, notification.tickerText, Toast.LENGTH_SHORT).show();
                Log.d("Tortuga","ticker: " + notification.tickerText);
                Log.d("Tortuga","icon: " + notification.icon);
                Log.d("Tortuga", "notification: "+ e.getText());
                Toast.makeText(this, "notification: "+ e.getText(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onInterrupt() {
        // TODO Auto-generated method stub
    }
}