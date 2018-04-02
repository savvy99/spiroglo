package com.kavi.spiroglo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ImageView mainImage;
    private TextView myMessage;
    private TextView xCood;
    private TextView yCood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myMessage  = (TextView) findViewById(R.id.MyMessage);
        xCood = (TextView) findViewById(R.id.Xcood);
        yCood = (TextView) findViewById(R.id.Ycood);

        mainImage = (ImageView) findViewById(R.id.mainImage);
        mainImage.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case(MotionEvent.ACTION_DOWN) :
                        myMessage.setText("Hi, I'm Kavi!");
                        xCood.setText("X: " + event.getX());
                        yCood.setText("Y:" + event.getY());
                        mainImage.invalidate();
                        return true;
                    case(MotionEvent.ACTION_MOVE) :
                        myMessage.setText("That tickles!");
                        xCood.setText("X: " + event.getX());
                        yCood.setText("Y:" + event.getY());
                        mainImage.invalidate();
                        return true;
                    case(MotionEvent.ACTION_UP) :
                        myMessage.setText("Try again!");
                        mainImage.invalidate();
                        return true;
                }
                return false;
            }
        });

        mainImage.setOnDragListener(new View.OnDragListener() {

            @Override
            public boolean onDrag(View v, DragEvent event) {
                myMessage.setText("That tickles!");
                xCood.setText("X: " + event.getX());
                yCood.setText("Y:" + event.getY());
                return false;
            }
        });
    }
}
