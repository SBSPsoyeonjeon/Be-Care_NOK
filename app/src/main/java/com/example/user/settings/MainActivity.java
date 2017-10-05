package com.example.user.settings;
import android.app.ActionBar;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;
public class MainActivity extends FragmentActivity implements ActionBar.TabListener
{
    ServerSocket serverSocket;
    String message_send = "";
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
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
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
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
        {
            @Override
            public void onPageSelected(int position)
            {
                actionBar.setSelectedNavigationItem(position);
            }
        }
        );
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++)
        {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        if (findViewById(R.id.pager) != null)
        {
            if (savedInstanceState != null)
            {
                return;
            }
            mapFragment = new MapFragment();
            mapFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.pager, mapFragment).commit();
        }
        if (savedInstanceState != null)
        {
            return;
        }
        database = 0;
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
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
    protected void onDestroy()
    {
        super.onDestroy();
        if (serverSocket != null)
        {
            try
            {
                serverSocket.close();
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction)
    {
        mViewPager.setCurrentItem(tab.getPosition());
    }
    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction)
    {
    }
    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction)
    {
    }
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {
        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }
        @Override
        public Fragment getItem(int position)
        {
            Fragment fragment;
            if (position==0)
            {
                fragment = new MapFragment();
                Bundle args = new Bundle();
                args.putInt(MapFragment.ARG_POSITION, position + 1);
                fragment.setArguments(args);
                mapFragment = (MapFragment) fragment;
            }
            else {
                fragment = new LogFragment();
                Bundle args = new Bundle();
                args.putInt(LogFragment.ARG_POSITION, position + 1);
                fragment.setArguments(args);
                logFragment = (LogFragment) fragment;
            }
            return fragment;
        }
        @Override
        public int getCount()
        {
            return 2;
        }
        @Override
        public CharSequence getPageTitle(int position)
        {
            Locale l = Locale.getDefault();
            switch (position)
            {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }
    // update the alarm details (on/off, vibrate, ringtone) from setting page
    public void updateStatus()
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean all = sharedPref.getBoolean(SettingsActivity.KEY_ALL, true);
        Boolean gas = sharedPref.getBoolean(SettingsActivity.KEY_GAS, true);
        Boolean door = sharedPref.getBoolean(SettingsActivity.KEY_DOOR, true);
        Boolean vibrate = sharedPref.getBoolean(SettingsActivity.KEY_VIBRATE, true);
        String sound = sharedPref.getString(SettingsActivity.KEY_RINGTONE, "");
        alarm_sound = sound;
        if(!all)
        {
            alarm_door = false;
            alarm_gas = false;
        }
       else
        {
            if(gas)
            {
                alarm_gas = true;
            } else
            {
                alarm_gas = false;
            }
            if(door)
            {
                alarm_door = true;
            } else
            {
                alarm_door = false;
            }
            if(vibrate)
            {
                alarm_vibrate = true;
            } else
            {
                alarm_vibrate = false;
            }
        }
    }
    private class SocketServerThread extends Thread
    {
        static final int SocketServerPORT = 8080;
        int count = 0;
        @Override
        public void run()
        {
            try
            {
                serverSocket = new ServerSocket(SocketServerPORT);
                while (message_send != "stop")
                {
                    Socket socket = serverSocket.accept();
                    SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(socket, count);
                    socketServerReplyThread.run();
                }
                message_send = "";
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    // receive the location / alarm message from Raspberry pi
    // change Map / Log page and make new notification depending on the newly received message
    private class SocketServerReplyThread extends Thread
    {
        private Socket hostThreadSocket;
        int cnt;
        SocketServerReplyThread(Socket socket, int c)
        {
            hostThreadSocket = socket;
            cnt = c;
        }
        @Override
        public void run()
        {
            int next;
            int notification;
            ObjectInputStream inputStream;
            ObjectOutputStream outputStream;
            String msgReply = "";
            try
            {
                inputStream = new ObjectInputStream(hostThreadSocket.getInputStream());
                outputStream = new ObjectOutputStream(hostThreadSocket.getOutputStream());
                outputStream.writeObject(message_send);
                msgReply += (String)inputStream.readObject();
                String[] tokens = msgReply.split("-");
                next = Integer.valueOf(tokens[0]);
                notification = Integer.valueOf(tokens[1]);
                database = 0;
                updateStatus();
                if( notification == 1)
                {
                    database = 1;
                    if(alarm_gas)
                    {
                        sendNotification(true);
                        mapFragment.changeMap(true, true);
                    }
                } else if( notification == 2)
                {
                    database = 2;
                    if(alarm_door)
                    {
                        sendNotification(false);
                        mapFragment.changeMap(true, false);
                    }
                } else
                {
                    mapFragment.changeMap(false, false);
                }
                previous = next;
                if(next == 1)
                {
                    mapFragment.changeMap(6);
                }else if(next == 2)
                {
                    mapFragment.changeMap(7);
                } else
                {
                    mapFragment.changeMap(next);
                }
                logFragment.changeLog(database);
            } catch (IOException e)
            {
                e.printStackTrace();
            } catch(ClassNotFoundException e)
            {
                e.getStackTrace();
            } catch (NumberFormatException e)
            {
                e.getStackTrace();
            }
        }
    }
    // make new notification with the alarm details (alarm type, time) and setting details (on/off, vibrate, ringtone)
    public void sendNotification(boolean mode)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        if(mode)
        {
            builder.setSmallIcon(R.drawable.notification_gas);
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.notification_gas));
            builder.setContentTitle("Be-Care System 알림: 화재");
            builder.setContentText("환자의 가스사용을 감지하였습니다!");
            builder.setSubText("자세한 내용을 보시려면 탭해주세요.");
            if(alarm_vibrate)
            {
                builder.setVibrate(new long[] { 500, 1000, 500, 1000, 500 });
            }else
            {
                builder.setVibrate(new long[] { 0 });
            }
            if(!alarm_sound.equals(R.string.pref_ringtone_silent))
            {
                builder.setSound(Uri.parse(alarm_sound));
            }
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_GAS, builder.build());
        } else
        {
            builder.setSmallIcon(R.drawable.notification_door);
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.notification_door));
            builder.setContentTitle("Be-Care System 알림: 배회");
            builder.setContentText("환자의 배회행동을 감지하였습니다!");
            builder.setSubText("자세한 내용을 보시려면 탭해주세요.");
            if(alarm_vibrate)
            {
                builder.setVibrate(new long[] { 500, 1000, 500, 1000, 500 });
            } else
            {
                builder.setVibrate(new long[] { 0 });
            }
            if(!alarm_sound.equals(R.string.pref_ringtone_silent))
            {
                builder.setSound(Uri.parse(alarm_sound));
            }
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_DOOR, builder.build());
        }
    }
}