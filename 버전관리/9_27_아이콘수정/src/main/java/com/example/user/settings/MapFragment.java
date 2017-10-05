/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.user.settings;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;

public class MapFragment extends Fragment {
    final static String ARG_POSITION = "position";
    int mCurrentPosition = -1;

    public ImageView room1;
    public ImageView gas;
    public ImageView door_open;
    public ImageView door_closed;

    public int previous = -1;
    public boolean flag = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.article_view, container, false);
        View view = inflater.inflate(R.layout.activity_main, container, false);

        room1 = (ImageView) view.findViewById(R.id.Room1);
        gas = (ImageView) view.findViewById(R.id.gas);
        door_open = (ImageView) view.findViewById(R.id.Open);
        door_closed = (ImageView) view.findViewById(R.id.Closed);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        Bundle args = getArguments();
        if (args != null) {
            // Set article based on argument passed in
            updateArticleView(args.getInt(ARG_POSITION));
        } else if (mCurrentPosition != -1) {
            // Set article based on saved instance state defined during onCreateView
            updateArticleView(mCurrentPosition);
        }
    }

    public void updateArticleView(int position) {
        mCurrentPosition = position;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current article selection in case we need to recreate the fragment
        outState.putInt(ARG_POSITION, mCurrentPosition);
    }

    // 나중에 next와 previous를 비교하는 코드를 javaClient에서 처리
    // 첫번째 값 = 0:위치변동X / 1-6:방 / 7-9:복도 / 10:문
    // 두번째 값 = 0:알림X / 1:화재알림 / 2:배회알림
    public void changeMap(int next){
        // new TranslateAnimation (float fromXDelta,float toXDelta, float fromYDelta, float toYDelta)
        final float R1_X = 100.0f;
        final float RL_X = 0.0f;
        final float RR_X = 800.0f;
        final float A_X = 400.0f;
        final float D_X = 150.0f;

        final float R1_Y = 0.0f;
        final float R2_Y = 450.0f;
        final float R3_Y = 850.0f;
        final float R5_Y = 500.0f;
        final float R6_Y = 1050.0f;
        final float A_Y = 450.0f;
        final float D_Y = 1230.0f;

        float fromXDelta = 0, toXDelta = 0, fromYDelta = 0, toYDelta = 0;

        if( (next != previous) ) {
            switch (previous) {
                case 1:
                    fromXDelta = R1_X;
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
                    fromYDelta = R1_Y;
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
                    fromXDelta = A_X;
                    fromYDelta = A_Y;
                    break;
                case 10:
                    fromXDelta = D_X;
                    fromYDelta = D_Y;
                    break;
                default:
                    break;
            }
            switch (next) {
                case 1:
                    toXDelta = R1_X;
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
                    toYDelta = R1_Y;
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
                    toXDelta = A_X;
                    toYDelta = A_Y;
                    break;
                case 10:
                    toXDelta = D_X;
                    toYDelta = D_Y;
                    break;
                default:
                    break;
            }

            final TranslateAnimation move = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    move.setDuration(1000);
                    move.setRepeatCount(0);
                    move.setFillAfter(true);
                    room1.startAnimation(move);
                }}
            );
            previous = next;
        }
    }

    public void changeMap(boolean alarm, boolean type){

        if(! alarm){

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    door_open.setVisibility(View.VISIBLE);
                    door_closed.setVisibility(View.INVISIBLE);
                    gas.setVisibility(View.INVISIBLE);
                }}
            );
        }

        else{

            if(type){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gas.setVisibility(View.VISIBLE);
                        door_open.setVisibility(View.VISIBLE);
                        door_closed.setVisibility(View.INVISIBLE);
                    }}
                );
            }
            else{
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gas.setVisibility(View.INVISIBLE);
                        door_open.setVisibility(View.INVISIBLE);
                        door_closed.setVisibility(View.VISIBLE);
                    }}
                );
            }
        }
    }
}