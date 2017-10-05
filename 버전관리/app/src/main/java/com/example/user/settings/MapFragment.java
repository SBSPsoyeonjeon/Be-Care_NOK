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

    public ImageView room1, room2, room3, room4, room5, room6;
    public ImageView alley1, alley2, alley3, door;

    public int previous = 11;

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
        room2 = (ImageView) view.findViewById(R.id.Room2);
        room3 = (ImageView) view.findViewById(R.id.Room3);
        room4 = (ImageView) view.findViewById(R.id.Room4);
        room5 = (ImageView) view.findViewById(R.id.Room5);
        room6 = (ImageView) view.findViewById(R.id.Room6);
        alley1 = (ImageView) view.findViewById(R.id.Alley1);
        alley2 = (ImageView) view.findViewById(R.id.Alley2);
        alley3 = (ImageView) view.findViewById(R.id.Alley3);
        door = (ImageView) view.findViewById(R.id.Door);
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

    // 첫번째 값 = 0:위치변동X / 1-6:방 / 7-9:복도 / 10:문
    // 두번째 값 = 0:알림X / 1:화재알림 / 2:배회알림
    public void changeMap(int next){
        if( (next != previous) && (next<11) ) {
            switch (next) {
                case 1:     getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {room1.setVisibility(View.VISIBLE);}});break;
                case 2:     getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {room2.setVisibility(View.VISIBLE);}});break;
                case 3:     getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {room3.setVisibility(View.VISIBLE);}});break;
                case 4:     getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {room4.setVisibility(View.VISIBLE);}});break;
                case 5:     getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {room5.setVisibility(View.VISIBLE);}});break;
                case 6:     getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {room6.setVisibility(View.VISIBLE);}});break;
                case 7:     getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {alley1.setVisibility(View.VISIBLE);}});break;
                case 8:     getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {alley2.setVisibility(View.VISIBLE);}});break;
                case 9:     getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {alley3.setVisibility(View.VISIBLE);}});break;
                case 10:    getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {door.setVisibility(View.VISIBLE);}});break;
                default:    break;
            }

            switch (previous) {
                case 1:     getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {room1.setVisibility(View.INVISIBLE);}});break;
                case 2:     getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {room2.setVisibility(View.INVISIBLE);}});break;
                case 3:     getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {room3.setVisibility(View.INVISIBLE);}});break;
                case 4:     getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {room4.setVisibility(View.INVISIBLE);}});break;
                case 5:     getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {room5.setVisibility(View.INVISIBLE);}});break;
                case 6:     getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {room6.setVisibility(View.INVISIBLE);}});break;
                case 7:     getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {alley1.setVisibility(View.INVISIBLE);}});break;
                case 8:     getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {alley2.setVisibility(View.INVISIBLE);}});break;
                case 9:     getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {alley3.setVisibility(View.INVISIBLE);}});break;
                case 10:    getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {door.setVisibility(View.INVISIBLE);}});break;
                default:    break;
            }
            previous = next;
        }
    }
}