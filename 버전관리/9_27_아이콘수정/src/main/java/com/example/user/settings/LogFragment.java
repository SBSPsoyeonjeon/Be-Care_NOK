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

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

public class LogFragment extends Fragment {

    public ListView mainListView ;
    public ArrayAdapter<String> listAdapter ;
    public int counter = 0;

    final static String ARG_POSITION = "position2";
    int mCurrentPosition = -1;

    Calendar cal;
    Date currentLocalTime;
    int currentHour,  currentMinutes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.article_view, container, false);
        View view = inflater.inflate(R.layout.activity_record, container, false);

        mainListView = (ListView) view.findViewById( R.id.mainListView );
        listAdapter = new ArrayAdapter<>(view.getContext(), R.layout.simplerow);
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

    public void changeLog(int database){
        if(database==1){
            cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+9:00"));
            currentLocalTime = cal.getTime();
            currentHour = cal.get(Calendar.HOUR);
            currentMinutes = cal.get(Calendar.MINUTE);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {listAdapter.insert( currentHour+"시 "+currentMinutes+"분" +" : 가스사용방지" , 0);}});
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {mainListView.setAdapter( listAdapter );}});
        }else if(database==2){
            cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+9:00"));
            currentLocalTime = cal.getTime();
            currentHour = cal.get(Calendar.HOUR);
            currentMinutes = cal.get(Calendar.MINUTE);

            getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {listAdapter.insert( currentHour+"시 "+currentMinutes+"분"  +" : 환자배회방지" , 0);}});
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {mainListView.setAdapter( listAdapter );}});
        }
    }
}