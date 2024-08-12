package com.viraj.threadproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomHandlerDemonstrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomHandlerDemonstrationFragment extends Fragment {

    private static final int SECONDS_TO_COUNT = 5;

    public CustomHandlerDemonstrationFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new CustomHandlerDemonstrationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private Button mBtnSendJob;

    private CustomHandler mCustomHandler;

    //Mechanism that allow to inject jobs from outside
    private final BlockingQueue<Runnable> mQueue=new LinkedBlockingQueue<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_custom_handler_demonstration, container, false);

        mBtnSendJob = view.findViewById(R.id.btn_send_job);
        mBtnSendJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendJob();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mCustomHandler=new CustomHandler();
    }

    @Override
    public void onStop() {
        super.onStop();
        mCustomHandler.stop();
    }

    private void sendJob(){
        mCustomHandler.post(new Runnable() {
            @Override
            public void run() {
                for (int i=0; i < SECONDS_TO_COUNT; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }
                    Log.d("CustomHandler", "iteration: " + i);
                }
            }
        });
    }

    private class CustomHandler{

        private final Runnable POISON=new Runnable() {
            @Override
            public void run(){
            }
        };
        private final BlockingQueue<Runnable> mQueue = new LinkedBlockingQueue<>();

        public CustomHandler() {
            initWorkerThread();
        }

        //Create one thread to do all work
        private void initWorkerThread() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("CustomHandler", "worker (looper) thread initialized");
                    while (true) {
                        Runnable runnable;
                        try {
    //1-1 runnable from Queue & once queue is empty take will pause this thread's execution until anything is available inside queue
                            runnable = mQueue.take();
                        } catch (InterruptedException e) {
                            return;
                        }
                        if (runnable == POISON) {
                            Log.d("CustomHandler", "poison data detected; stopping working thread");
                            return;
                        }
                        runnable.run();
                    }
                }
            }).start();
        }
        public void post(Runnable job) {
            mQueue.add(job);
        }

        public void stop() {
            mQueue.clear();
            mQueue.add(POISON);
        }
    }
}