package com.example.user.settings;

import android.app.ActionBar;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.Buffer;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    Socket socket;

    public int previous = 11;
    public int database;

    public static final int NOTIFICATION_GAS = 1;
    public static final int NOTIFICATION_DOOR = 1;

    MapFragment mapFragment;
    LogFragment logFragment;

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    Bundle bundle;

    public boolean alarm_gas = true;
    public boolean alarm_door = false;
    public boolean alarm_vibrate = false;
    public String alarm_sound;

    SocketClientThread socketClient;
    public static final String SocketServer= "192.168.43.119";
    public static final int SocketServerPORT = 13150;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        bundle = savedInstanceState;
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.sample_main);

        final ActionBar actionBar = getActionBar();

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.mint)) );

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        if (findViewById(R.id.pager) != null) {
            if (savedInstanceState != null) {
                return;
            }

            mapFragment = new MapFragment();
            mapFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.pager, mapFragment).commit();
        }

        if (savedInstanceState != null) {
            return;
        }

        socketClient = new SocketClientThread();
        runnable.run();
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            Thread socketClientThread = new Thread(socketClient);
            socketClientThread.start();

            handler.postDelayed(this, 5000);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }
    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
    }
    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        // END_INCLUDE (fragment_pager_adapter)

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // BEGIN_INCLUDE (fragment_pager_adapter_getitem)
        /**
         * Get fragment corresponding to a specific position. This will be used to populate the
         * contents of the {@link ViewPager}.
         *
         * @param position Position to fetch fragment for.
         * @return Fragment for specified position.
         */
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            Fragment fragment;
            if (position==0){
                fragment = new MapFragment();
                Bundle args = new Bundle();
                args.putInt(MapFragment.ARG_POSITION, position + 1);
                fragment.setArguments(args);

                mapFragment = (MapFragment) fragment;
            }else{
                fragment = new LogFragment();
                Bundle args = new Bundle();
                args.putInt(LogFragment.ARG_POSITION, position + 1);
                fragment.setArguments(args);

                logFragment = (LogFragment) fragment;
            }
            return fragment;
        }
        // END_INCLUDE (fragment_pager_adapter_getitem)

        // BEGIN_INCLUDE (fragment_pager_adapter_getcount)
        /**
         * Get number of pages the {@link ViewPager} should render.
         *
         * @return Number of fragments to be rendered as pages.
         */
        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
        // END_INCLUDE (fragment_pager_adapter_getcount)

        // BEGIN_INCLUDE (fragment_pager_adapter_getpagetitle)
        /**
         * Get title for each of the pages. This will be displayed on each of the tabs.
         *
         * @param position Page to fetch title for.
         * @return Title for specified page.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
        // END_INCLUDE (fragment_pager_adapter_getpagetitle)
    }

    public void updateStatus(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean all = sharedPref.getBoolean(SettingsActivity.KEY_ALL, true);
        Boolean gas = sharedPref.getBoolean(SettingsActivity.KEY_GAS, true);
        Boolean door = sharedPref.getBoolean(SettingsActivity.KEY_DOOR, true);
        Boolean vibrate = sharedPref.getBoolean(SettingsActivity.KEY_VIBRATE, true);
        String sound = sharedPref.getString(SettingsActivity.KEY_RINGTONE, "");
        alarm_sound = sound;

        if(!all){
            alarm_door = false;
            alarm_gas = false;
        }else{
            if(gas){
                alarm_gas = true;
            }else{
                alarm_gas = false;
            }
            if(door){
                alarm_door = true;
            }else{
                alarm_door = false;
            }
            if(vibrate){
                alarm_vibrate = true;
            }else{
                alarm_vibrate = false;
            }
        }
    }

    private class SocketClientThread extends Thread {

        ObjectInputStream inputStream;
        ObjectOutputStream outputStream;

        int next;
        int notification;

        public SocketClientThread(){

            new Thread(){
                @Override
                public void run(){
                    socket = null;
                    inputStream = null;
                    try{
                        socket = new Socket(SocketServer, SocketServerPORT);
                        if(socket != null){
                            inputStream = new ObjectInputStream(socket.getInputStream());
                            outputStream = new ObjectOutputStream(socket.getOutputStream());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        @Override
        public void run() {
            String msgReply = "";

            try {
                if( inputStream != null ){

                    outputStream.writeObject("fuck!");
                    msgReply = (String)inputStream.readObject();
                    inputStream.close();

                    String[] tokens = msgReply.split("-");
                    next = Integer.valueOf(tokens[0]);
                    notification = Integer.valueOf(tokens[1]);

                    updateStatus();
                    updateChange();
                }
            } catch (NumberFormatException e){
                e.getStackTrace();
            } catch(ClassNotFoundException e){
                e.getStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void updateChange(){

            database = 0;

            if (notification == 1) {
                database = 1;
                if (alarm_gas) {
                    sendNotification(true);
                }
            }
            else if (notification == 2) {
                database = 2;
                if (alarm_door) {
                    sendNotification(false);
                }
            }

            previous = next;

            if(mapFragment != null){
                mapFragment.changeMap(next);
            }
            if(logFragment != null){
                logFragment.changeLog(database);
            }
        }
    }

    public void sendNotification(boolean mode) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        //Create Intent to launch this Activity again if the notification is clicked.
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if(mode){
            builder.setSmallIcon(R.drawable.notification_gas);
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.notification_gas));
            builder.setContentTitle("Be-Care System 알림: 화재");
            builder.setContentText("환자의 가스사용을 감지하였습니다!");
            builder.setSubText("자세한 내용을 보시려면 탭해주세요.");

            if(alarm_vibrate){
                builder.setVibrate(new long[] { 500, 1000, 500, 1000, 500 });
            }
            if(!alarm_sound.equals(R.string.pref_ringtone_silent)){
                builder.setSound(Uri.parse(alarm_sound));
            }

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_GAS, builder.build());
        } else{
            builder.setSmallIcon(R.drawable.notification_door);
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.notification_door));
            builder.setContentTitle("Be-Care System 알림: 배회");
            builder.setContentText("환자의 배회행동을 감지하였습니다!");
            builder.setSubText("자세한 내용을 보시려면 탭해주세요.");

            if(alarm_vibrate){
                builder.setVibrate(new long[] { 500, 1000, 500, 1000, 500 });
            }
            if(!alarm_sound.equals(R.string.pref_ringtone_silent)){
                builder.setSound(Uri.parse(alarm_sound));
            }

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_DOOR, builder.build());
        }

    }

}