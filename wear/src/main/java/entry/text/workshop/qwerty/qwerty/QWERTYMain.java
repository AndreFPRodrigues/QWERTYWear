package entry.text.workshop.qwerty.qwerty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import entry.text.workshop.qwerty.R;
import entry.text.workshop.qwerty.StudyListener;


public class QWERTYMain extends Activity {


    private final int HUGE = 0;
    private final int LARGE = 1;
    private final int MEDIUM = 2;
    private final int SMALL = 3;
    //private EditText editText;
    private StudyListener sl;
    private KeyboardQWERTY kq;
    private LinearLayout container;
    private boolean create = true;
    private String lastLetter;
    private String phrase;
    private GestureDetectorCompat mDetector;
    private boolean adapt;
    private int keyboard;

    private boolean timeGuard;
    private long locked;
    private String lockedLetter;
    private final long LOCK_THRESHOLD=100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sl = new StudyListener();
        Log.d("teste", "INIT");
        phrase = "";
        lastLetter = "";
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        Intent i = getIntent();
        adapt = i.getBooleanExtra("adapt", false);
        timeGuard=adapt;

        keyboard = i.getIntExtra("keyboard", 1);
        switch (keyboard) {
            case HUGE:
                stub.setRoundLayout(R.layout.round_activity_huge);
                break;
            case LARGE:
                stub.setRoundLayout(R.layout.round_activity_large);
                break;
            case MEDIUM:
                stub.setRoundLayout(R.layout.round_activity_medium);
                break;
            case SMALL:
                stub.setRoundLayout(R.layout.round_activity_small);
                break;
        }


        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                //  editText = (EditText) stub.findViewById(R.id.text);
                //   HorizontalScrollView container = (HorizontalScrollView) stub.findViewById(R.id.container);
                container = (LinearLayout) stub.findViewById(R.id.ll0);

                container.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        //create bounds of all keys to check which letter is hovered
                        if (create) {
                            create = false;
                            kq = new KeyboardQWERTY(container, adapt);

                        }
                        if (!mDetector.onTouchEvent(motionEvent)) {
                            // Log.d("teste", motionEvent.toString());


                            switch (motionEvent.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    lastLetter = kq.getCharacter((int) motionEvent.getX(), (int) motionEvent.getY());
                                    sl.sendMessage(getApplicationContext(), StudyListener.TO_READ, lastLetter);
                                    locked = System.currentTimeMillis();
                                    lockedLetter=lastLetter;

                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    String letter = kq.getCharacter((int) motionEvent.getX(), (int) motionEvent.getY());
                                    if (!lastLetter.equals(letter)) {

                                        lockedLetter=lastLetter;
                                        lastLetter = letter;
                                        locked = System.currentTimeMillis();
                                        Log.d("teste", "Locked:" + lockedLetter + " current:" + lastLetter);

                                        sl.sendMessage(getApplicationContext(), StudyListener.TO_READ, lastLetter );
                                    }
                                    break;
                                case MotionEvent.ACTION_UP:
                                    if (!lastLetter.equals("")) {

                                        if(timeGuard &&(System.currentTimeMillis()-locked)<LOCK_THRESHOLD) {
                                            locked = System.currentTimeMillis();
                                            Log.d("teste", "Corrected:" + lastLetter + " to:" + lockedLetter);

                                            lastLetter=lockedLetter;
                                        }
                                        sl.sendMessage(getApplicationContext(), StudyListener.TO_WRITE, lastLetter);
                                        if (lastLetter.equals("espasso"))
                                            lastLetter = " ";
                                        phrase += lastLetter;
                                        // editText.setText(phrase);
                                        sl.sendMessage(getApplicationContext(), StudyListener.INIT, keyboard + "," + adapt);
                                        kq.clearBounds();
                                    }
                                    break;
                            }
                            sl.sendMessage(getApplicationContext(), StudyListener.IOLOG, motionEvent.getX() + "," + motionEvent.getY() + "," + motionEvent.getEventTime() + "," + motionEvent.getPressure() + ","
                                    + motionEvent.getSize() + "," + motionEvent.getAction() + "," + System.currentTimeMillis());
                        }
                        return true;
                    }


                });
            }
        });


    }

    private void delete() {
        sl.sendMessage(getApplicationContext(), StudyListener.TO_WRITE, "apagar");

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
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
            delete();

            return true;
        }
    }


}
