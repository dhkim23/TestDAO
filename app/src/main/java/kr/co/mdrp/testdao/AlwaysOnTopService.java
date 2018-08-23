package kr.co.mdrp.testdao;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by sjkim on 2016. 8. 21..
 */
public class AlwaysOnTopService extends Service implements View.OnTouchListener, View.OnClickListener {
    private View mView;
    //private Button overlayedButton;
    View overlayedButton ;
    Button mBStart;
    private double mLat, mLng;

    //for notification


    private float offsetX;
    private float offsetY;
    private int originalXPos;
    private int originalYPos;
    private boolean moving;
    private WindowManager wm;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        LayoutInflater inflater;
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        overlayedButton = inflater.inflate(R.layout.always_top,null);

        mBStart = overlayedButton.findViewById(R.id.btnStart);
        mBStart.setOnClickListener(this);

        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        //overlayedButton = new Button(this);
        //overlayedButton.setText("Overlay mBStart");
        overlayedButton.setOnTouchListener(this);
        //overlayedButton.setAlpha(0.0f);
        //overlayedButton.setBackgroundColor(0x55fe4444);
        overlayedButton.setOnClickListener(this);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                /*WindowManager.LayoutParams.MATCH_PARENT*/300,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,

                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.START | Gravity.TOP;
        //params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
        params.x = 0;
        params.y = 0;



        wm.addView(overlayedButton, params);



        mView = new View(this);
        WindowManager.LayoutParams topLeftParams =
                new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                |WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                |WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                        PixelFormat.TRANSLUCENT);
        topLeftParams.gravity = Gravity.START | Gravity.TOP;
        topLeftParams.x = 0;
        topLeftParams.y = 0;
        topLeftParams.width = 0;
        topLeftParams.height = 0;
        wm.addView(mView, topLeftParams);







    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startNotification();
        return super.onStartCommand(intent,flags,startId);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(wm!=null){

        }
        if(mView!=null){
            wm.removeView(mView);
            mView = null;
        }

        if (overlayedButton != null) {
            wm.removeView(overlayedButton);
            overlayedButton = null;

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getRawX();
            float y = event.getRawY();

            moving = false;

            int[] location = new int[2];
            overlayedButton.getLocationOnScreen(location);

            originalXPos = location[0];
            originalYPos = location[1];

            offsetX = originalXPos - x;
            offsetY = originalYPos - y;

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int[] topLeftLocationOnScreen = new int[2];
            mView.getLocationOnScreen(topLeftLocationOnScreen);

            System.out.println("topLeftY="+topLeftLocationOnScreen[1]);
            System.out.println("originalY="+originalYPos);

            float x = event.getRawX();
            float y = event.getRawY();

            WindowManager.LayoutParams params = (WindowManager.LayoutParams) overlayedButton.getLayoutParams();

            int newX = (int) (offsetX + x);
            int newY = (int) (offsetY + y);

            if (Math.abs(newX - originalXPos) < 1 && Math.abs(newY - originalYPos) < 1 && !moving) {
                return false;
            }

            params.x = newX - (topLeftLocationOnScreen[0]);
            params.y = newY - (topLeftLocationOnScreen[1]);

            wm.updateViewLayout(overlayedButton, params);
            moving = true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            return moving;
        }

        return false;
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btnStart){
            Toast.makeText(this, "Service Stop", Toast.LENGTH_SHORT).show();

            //mService.myServiceFunc();

        }else{
            Toast.makeText(this, "Overlay mBStart click event", Toast.LENGTH_SHORT).show();
        }

    }


    protected void startNotification() {
        // TODO Auto-generated method stub
        //Creating Notification Builder

//        Resources res = getResources();
//        Intent notificationIntent = new Intent(this, InputActivity.class);
//        notificationIntent.putExtra("notificationId", 9999); //전달할 값
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        NotificationCompat.Builder builder;
//        builder = new NotificationCompat.Builder(getApplicationContext())
//                .setContentTitle("상태바 드래그시 보이는 타이틀")
//                .setContentText("상태바 드래그시 보이는 서브타이틀")
//                .setTicker("상태바 한줄 메시지")
//                .setSmallIcon(R.drawable.ic_title_white_24dp)
//                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_title_white_24dp))
//                .setContentIntent(contentIntent)
//                .setAutoCancel(true)
//                .setWhen(System.currentTimeMillis())
//                .setDefaults( Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE|Notification.DEFAULT_LIGHTS)
//                .setNumber(13);
//
//        Notification  n = builder.build();
//        startForeground(1234,n);

        //NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //nm.notify(1234, n);

    }
}
