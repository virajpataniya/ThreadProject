package com.viraj.threadproject.Exercises;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viraj.threadproject.R;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Excerise2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Excerise2 extends Fragment {

    private byte[] mDummyData;
    private AtomicBoolean mfetchAborted=new AtomicBoolean(false);

    public Excerise2() {
        // Required empty public constructor
    }

    public static Excerise2 newInstance(String param1, String param2) {
        Excerise2 fragment = new Excerise2();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDummyData = new byte[50 * 1000 * 1000];
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_excerise2, container, false);
    }


    @Override
    public void onStart() {
        super.onStart();
        countScreenTime();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Set the flag to pause or stop ongoing tasks temporarily
        mfetchAborted.set(true);
    }

    private void countScreenTime() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int screenTimeSeconds = 0;
                while (!mfetchAborted.get()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }
                    screenTimeSeconds++;
                    Log.d("Exercise 2", "screen time: " + screenTimeSeconds + "s");
                }
            }
        }).start();
    }
}