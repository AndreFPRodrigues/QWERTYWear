package entry.text.workshop.qwerty.typebraille;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import entry.text.workshop.qwerty.R;
import entry.text.workshop.qwerty.StudyListener;


public class TypeInBraille extends Activity {
    private final String TAG = "BrailleType";
    private StudyListener sl;
    private LinearLayout container;
    private GestureDetectorCompat mDetector;
    private BrailleLine current;
    private BrailleLetter letter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_in_braille);
        sl = new StudyListener();
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        Log.d(TAG, "INIT");


    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        sl.sendMessage(getApplicationContext(), StudyListener.IOLOG, event.getX() + "," + event.getY() + "," + event.getEventTime() + "," + event.getPressure() + "," + event.getSize() + "," + event.getAction());

        // this.mDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (current == null)
                    current = new BrailleLine(event);
                break;
            case MotionEvent.ACTION_UP:
                switch (current.getType()) {
                    case BrailleLine.NEXT_CHAR:
                        shift();
                        break;
                    default:
                        if (current != null) {
                            if (letter != null) {

                                if (letter.addLine(current)) {
                                    String toWrite = letter.getLetter();

                                    sl.sendMessage(getApplicationContext(), StudyListener.TO_WRITE, toWrite);
                                    Log.d(TAG, toWrite);

                                    letter = null;
                                }
                            } else {
                                letter = new BrailleLetter(current);
                            }
                            sl.sendMessage(getApplicationContext(), StudyListener.LOG, current.getType()+"");

                            current = null;

                        }
                        break;
                }
                break;
            default:
                if (current != null) {
                    current.processEvent(event);
                }
                break;
        }


        return super.onTouchEvent(event);
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        sl.sendMessage(getApplicationContext(), StudyListener.IOLOG, event.getX() + "," + event.getY() + "," + event.getEventTime() + "," + event.getPressure() + "," + event.getSize() + "," + event.getAction());

        this.mDetector.onTouchEvent(event);
        Log.d(TAG, event.toString());

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (current != null) {
                if (letter != null) {
                    if (letter.addLine(current)) {
                        String toWrite = letter.getLetter();

                            sl.sendMessage(getApplicationContext(), StudyListener.TO_WRITE, toWrite);
                            Log.d(TAG, toWrite);

                        letter = null;
                        current = null;
                    }
                } else {
                    letter = new BrailleLetter(current);
                }
            }
            current = new BrailleLine(event);
        } else {
            if (current != null) {
                current.processEvent(event);
            }
        }

        return super.onTouchEvent(event);
    }*/

    private void shift() {
        current = null;
        if (letter != null) {
            String toWrite = letter.getLetter();
            sl.sendMessage(getApplicationContext(), StudyListener.TO_WRITE, toWrite);
            Log.d(TAG, toWrite);

            letter = null;
        } else {
            String toWrite = "espasso";
            sl.sendMessage(getApplicationContext(), StudyListener.TO_WRITE, toWrite);
            Log.d(TAG, toWrite);
        }
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";


        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d(TAG, "Fling");

            if (Math.abs(velocityY) > Math.abs(velocityX)) {
                if (velocityY > 0)
                    current.empty();
            } else {
                if (velocityX > 0)
                    shift();
            }
            return true;
        }
    }
}
