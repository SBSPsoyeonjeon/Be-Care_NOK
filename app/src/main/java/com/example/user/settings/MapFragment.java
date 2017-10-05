package com.example.user.settings;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
public class MapFragment extends Fragment
{
    final static String ARG_POSITION = "position";
    int mCurrentPosition = -1;
    public ImageView room1;
    public ImageView gas;
    public ImageView door_open;
    public ImageView door_closed;
    public int previous = -1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }
        View view = inflater.inflate(R.layout.activity_main, container, false);
        room1 = (ImageView) view.findViewById(R.id.Room1);
        gas = (ImageView) view.findViewById(R.id.gas);
        door_open = (ImageView) view.findViewById(R.id.Open);
        door_closed = (ImageView) view.findViewById(R.id.Closed);
        return view;
    }
    @Override
    public void onStart()
    {
        super.onStart();
        Bundle args = getArguments();
        if (args != null)
        {
            updateArticleView(args.getInt(ARG_POSITION));
        } else if (mCurrentPosition != -1)
        {
            updateArticleView(mCurrentPosition);
        }
    }
    public void updateArticleView(int position)
    {
        mCurrentPosition = position;
    }
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_POSITION, mCurrentPosition);
    }
    // change Map page with the newly received location message(patient's location) from Raspberry pi
    // animate the patient icon from previous location to new location for 1 second
    public void changeMap(int next)
    {
        float fromXDelta = 0, toXDelta = 0, fromYDelta = 0, toYDelta = 0;
        final float RL_X = 0.0f;
        final float RR_X = 800.0f;
        final float D_X = 150.0f;
        final float R1_Y = 0.0f;
        final float R2_Y = 450.0f;
        final float R3_Y = 850.0f;
        final float R4_Y = 0.0f;
        final float R5_Y = 500.0f;
        final float R6_Y = 1025.0f;
        final float D_Y = 1230.0f;
        if( (next != previous) )
        {
            switch (previous)
            {
                case 1:
                    fromXDelta = RL_X;
                    fromYDelta = R1_Y;
                    break;
                case 2:
                    fromXDelta = RL_X;
                    fromYDelta = R2_Y;
                    break;
                case 3:
                    fromXDelta = RL_X;
                    fromYDelta = R3_Y;
                    break;
                case 4:
                    fromXDelta = RR_X;
                    fromYDelta = R4_Y;
                    break;
                case 5:
                    fromXDelta = RR_X;
                    fromYDelta = R5_Y;
                    break;
                case 6:
                    fromXDelta = RR_X;
                    fromYDelta = R6_Y;
                    break;
                case 7:
                    fromXDelta = D_X;
                    fromYDelta = D_Y;
                    break;
                default:
                    break;
            }
            switch (next)
            {
                case 1:
                    toXDelta = RL_X;
                    toYDelta = R1_Y;
                    break;
                case 2:
                    toXDelta = RL_X;
                    toYDelta = R2_Y;
                    break;
                case 3:
                    toXDelta = RL_X;
                    toYDelta = R3_Y;
                    break;
                case 4:
                    toXDelta = RR_X;
                    toYDelta = R4_Y;
                    break;
                case 5:
                    toXDelta = RR_X;
                    toYDelta = R5_Y;
                    break;
                case 6:
                    toXDelta = RR_X;
                    toYDelta = R6_Y;
                    break;
                case 7:
                    toXDelta = D_X;
                    toYDelta = D_Y;
                    break;
                default:
                    break;
            }
            final TranslateAnimation move = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    move.setDuration(1000);
                    move.setRepeatCount(0);
                    move.setFillAfter(true);
                    room1.startAnimation(move);
                }
            }
            );
            previous = next;
        }
    }
    // change Map page with the newly received alarm message(alarm type) from Raspberry pi
    // set gas & closed door icon invisible if there's no alarm
    // set gas icon visible if there's fire detection
    // set closed door icon visible if there's movement detection at main door
    public void changeMap(boolean alarm, boolean type)
    {
        if(!alarm)
        {   // no alarm
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    door_open.setVisibility(View.VISIBLE);
                    door_closed.setVisibility(View.INVISIBLE);
                    gas.setVisibility(View.INVISIBLE);
                }
            }
            );
        }
        else
        {
            if(type)
            {   // fire detection
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        gas.setVisibility(View.VISIBLE);
                        door_open.setVisibility(View.VISIBLE);
                        door_closed.setVisibility(View.INVISIBLE);
                    }
                }
                );
            }
            else
            {   // movement detection
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        gas.setVisibility(View.INVISIBLE);
                        door_open.setVisibility(View.INVISIBLE);
                        door_closed.setVisibility(View.VISIBLE);
                    }
                }
                );
            }
        }
    }
}