package com.kavi.spiroglo;

import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
    private OutputHandler outputHandler;

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

        outputHandler = new OutputHandler() {
            @Override
            public void processOutput(double speed, RotationalDirection direction) {
                txtSpeed.setText(Double.toString(speed));
                txtDirection.setText(direction.name());
                mainImage.invalidate();
            }
        };

        mainImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean eventProcessed = false;
                switch(event.getAction()) {
                    case(MotionEvent.ACTION_DOWN) :
                        //myMessage.setText("Hi, I'm Kavi!");
                        //mainImage.invalidate();
                        eventProcessed = updateSpeedChecker(v, event);
                        break;
                    case(MotionEvent.ACTION_MOVE) :
                        //myMessage.setText("That tickles!");
                        //mainImage.invalidate();
                        eventProcessed = updateSpeedChecker(v, event);
                        break;
                    case(MotionEvent.ACTION_UP) :
                        //myMessage.setText("Try again!");
                        //mainImage.invalidate();
                        eventProcessed = updateSpeedChecker(v, event);
                        break;
                }
                return eventProcessed;
            }
        });

        speedChecker = new SpeedCheck(10);
        speedChecker.start();
    }

    private boolean updateSpeedChecker(View v, MotionEvent event) {
        speedChecker.xNow = event.getX() + v.getLeft();
        speedChecker.yNow = event.getY() + v.getTop();
        speedChecker.quadrant = determineQuadrant(v, speedChecker.xNow, speedChecker.yNow);
        return true;
    }

    private Quadrant determineQuadrant(View v, double x, double y) {
        Quadrant quadrant;
        Rect rectf = new Rect();
        v.getGlobalVisibleRect(rectf);
        if(x < rectf.width()/2) {
            if(y < rectf.height()/2) {
                quadrant = Quadrant.TOP_LEFT;
            }
            else {
                quadrant = Quadrant.BOTTOM_LEFT;;
            }
        }
        else {
            if(y < rectf.height()/2) {
                quadrant = Quadrant.TOP_RIGHT;
            }
            else {
                quadrant = Quadrant.BOTTOM_RIGHT;
            }
        }
        return quadrant;
    }


    private class SpeedCheck {

        long delay;

        private volatile double xNow;
        private volatile double yNow;
        private volatile Quadrant quadrant;

        private Direction direction;
        private double xPrevious;
        private double yPrevious;

        private boolean stop = false;
        private Handler tickEventHandler = new Handler();
        private Runnable tickTockEvent = new Runnable() {
            @Override
            public void run() {
                double dx = xNow - xPrevious;
                double dy = yNow - yPrevious;
                direction = determineDirection(dx, dy);
                xPrevious = xNow;
                yPrevious = yNow;
                double distance = Math.sqrt(dx * dx + dy * dy);
                RotationalDirection rd = determineRotaionalDirection(quadrant, direction);
                MainActivity.this.outputHandler.processOutput(distance, rd);
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

        private Direction determineDirection(double dx, double dy) {
            Direction direction;
            if(dx == 0) {
                if(dy == 0) {
                    direction = Direction.STATIONARY;
                }
                else if(dy > 0) {
                    direction = Direction.SOUTH;
                }
                else {
                    direction = Direction.NORTH;
                }
            }
            else if(dx > 0) {
                if(dy == 0) {
                    direction = Direction.EAST;
                }
                else if(dy > 0) {
                    direction = Direction.SOUTH_EAST;
                }
                else {
                    direction = Direction.NORTH_EAST;
                }
            }
            else {
                if(dy == 0) {
                    direction = Direction.WEST;
                }
                else if(dy > 0) {
                    direction = Direction.SOUTH_WEST;
                }
                else {
                    direction = Direction.NORTH_WEST;
                }
            }
            return direction;
        }

        private RotationalDirection determineRotaionalDirection(Quadrant q, Direction d) {

            RotationalDirection rd = RotationalDirection.STILL;
            if(d != Direction.STATIONARY) {
                switch (q) {
                    case TOP_LEFT:
                        if (d == Direction.NORTH || d == Direction.EAST || d == Direction.NORTH_EAST || d == Direction.NORTH_WEST) {
                            rd = RotationalDirection.CLOCKWISE;
                        } else {
                            rd = RotationalDirection.ANTI_CLOCKWISE;
                        }
                        break;
                    case TOP_RIGHT:
                        if (d == Direction.SOUTH || d == Direction.EAST || d == Direction.SOUTH_EAST || d == Direction.SOUTH_WEST) {
                            rd = RotationalDirection.CLOCKWISE;
                        } else {
                            rd = RotationalDirection.ANTI_CLOCKWISE;
                        }
                        break;
                    case BOTTOM_LEFT:
                        if (d == Direction.NORTH || d == Direction.WEST || d == Direction.SOUTH_EAST || d == Direction.SOUTH_WEST) {
                            rd = RotationalDirection.CLOCKWISE;
                        } else {
                            rd = RotationalDirection.ANTI_CLOCKWISE;
                        }
                        break;
                    case BOTTOM_RIGHT:
                        if (d == Direction.SOUTH || d == Direction.WEST || d == Direction.NORTH_EAST || d == Direction.NORTH_WEST) {
                            rd = RotationalDirection.CLOCKWISE;
                        } else {
                            rd = RotationalDirection.ANTI_CLOCKWISE;
                        }
                        break;
                }
            }
            return rd;
        }
    }
}
