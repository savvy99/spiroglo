package com.kavi.spiroglo;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ImageView mainImage;
    private TextView myMessage;
    private TextView txtSpeed;
    private TextView txtDirection;

    private SpeedCheck speedChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainImage = (ImageView) findViewById(R.id.mainImage);
        myMessage  = (TextView) findViewById(R.id.MyMessage);
        txtSpeed = (TextView) findViewById(R.id.txtSpeed);
        txtDirection = (TextView) findViewById(R.id.txtDirection);

        mainImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean eventProcessed = false;
                switch(event.getAction()) {
                    case(MotionEvent.ACTION_DOWN) :
                        //myMessage.setText("Hi, I'm Kavi!");
                        //mainImage.invalidate();
                        speedChecker.xNow = event.getX() + v.getLeft();
                        speedChecker.yNow = event.getY() + v.getTop();
                        eventProcessed = true;
                        break;
                    case(MotionEvent.ACTION_MOVE) :
                        //myMessage.setText("That tickles!");
                        //mainImage.invalidate();
                        speedChecker.xNow = event.getX() + v.getLeft();
                        speedChecker.yNow = event.getY() + v.getTop();
                        eventProcessed = true;
                        break;
                    case(MotionEvent.ACTION_UP) :
                        //myMessage.setText("Try again!");
                        //mainImage.invalidate();
                        speedChecker.xNow = event.getX() + v.getLeft();
                        speedChecker.yNow = event.getY() + v.getTop();
                        eventProcessed = true;
                        break;
                }
                return eventProcessed;
            }
        });

        speedChecker = new SpeedCheck(10);
        speedChecker.start();
    }

    private class SpeedCheck {

        long delay;

        private volatile double xNow;
        private volatile double yNow;
        private double xPrevious;
        private double yPrevious;

        private boolean stop = false;
        private Handler tickEventHandler = new Handler();
        private Runnable tickTockEvent = new Runnable() {
            @Override
            public void run() {
                double dx = xNow - xPrevious;
                double dy = yNow - yPrevious;
                xPrevious = xNow;
                yPrevious = yNow;
                double distance = Math.sqrt(dx * dx + dy * dy);
                txtSpeed.setText(Double.toString(distance));
                mainImage.invalidate();
                if(!stop) {
                    tickEventHandler.postDelayed(this, delay);
                }
            }
        };

        SpeedCheck(long delayMillis) {
            this.delay = delayMillis;

        }

        void start() {
            tickEventHandler.post(tickTockEvent);
        }

        void stop() {
            stop = true;
        }
    }
}
