package com.viraj.threadproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class MainActivity extends AppCompatActivity {

    Button countIteration;
    private static final int ITERATIONS_COUNTERS_DURATIONS_SEC=10;
    private ExecutorService executorService;
    private Handler mUiHandler=new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        countIteration=findViewById(R.id.countIteration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        countIteration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ExecutorService is Method 1 to prevent from memory leakage
                if (executorService == null) {
                    executorService = Executors.newSingleThreadExecutor();
                }
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        countIteration();
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService!=null){
            executorService.shutdown();
        }
    }

    private void countIteration(){
        long startTimeStamp=System.currentTimeMillis();
        long endTimeStamp=startTimeStamp+ITERATIONS_COUNTERS_DURATIONS_SEC*1000;
        int iterationsCount=0;
        while (System.currentTimeMillis()<=endTimeStamp){
            iterationsCount++;
        }
        Log.d(
                "Viraj",
                "iterations in "+ITERATIONS_COUNTERS_DURATIONS_SEC+" seconds: "+iterationsCount
        );

        //Handler is Method 2 to prevent from memory leakage/ANR
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("Viraj","Thread name "+Thread.currentThread().getName());
                // Code to run on the UI thread
                countIteration.setText("Updated from background thread");
            }
        });

    }
}