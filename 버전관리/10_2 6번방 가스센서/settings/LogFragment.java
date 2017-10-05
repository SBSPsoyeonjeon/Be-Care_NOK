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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class LogFragment extends Fragment
{
    public ListView mainListView ;
    public ArrayAdapter<String> listAdapter ;

    final static String ARG_POSITION = "position2";
    int mCurrentPosition = -1;

    Calendar cal;
    Date currentLocalTime;
    int currentHour,  currentMinutes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }

        View view = inflater.inflate(R.layout.activity_record, container, false);

        mainListView = (ListView) view.findViewById( R.id.mainListView );
        listAdapter = new ArrayAdapter<>(view.getContext(), R.layout.simplerow);
        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Bundle args = getArguments();

        if (args != null) {
            updateArticleView(args.getInt(ARG_POSITION));
        } else if (mCurrentPosition != -1) {
            updateArticleView(mCurrentPosition);
        }
    }

    public void updateArticleView(int position) {
        mCurrentPosition = position;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_POSITION, mCurrentPosition);
    }

    public void changeLog(int database)
    {
        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+9:00"));
        currentLocalTime = cal.getTime();
        currentHour = cal.get(Calendar.HOUR);
        currentMinutes = cal.get(Calendar.MINUTE);

        if(database==1) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {listAdapter.insert( currentHour+"시 "+currentMinutes+"분" +" :  가스사용방지" , 0);}});
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {mainListView.setAdapter( listAdapter );}});

        } else if(database==2) {
            getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {listAdapter.insert( currentHour+"시 "+currentMinutes+"분"  +" :  환자배회방지" , 0);}});
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {mainListView.setAdapter( listAdapter );}});
        }
    }
}