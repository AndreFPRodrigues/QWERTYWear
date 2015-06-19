package entry.text.workshop.qwerty;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;

import storage.Logger;

public class MainActivity extends Activity {

    private final String TO_READ = "toRead";
    private EditText editText;
    private StudyListener sl;
    private KeyboardQWERTY kq;
    private LinearLayout container;
    private boolean create = true;
    private String lastLetter;
    private String phrase;

    private static String IOLog = "EventLog";
    private Logger mEventLogger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sl = new StudyListener();
        Log.d("teste", "INIT");
        phrase = "";
        lastLetter="";
        //mEventLogger = new Logger(IOLog,50,System.currentTimeMillis(),Logger.FileFormat.txt);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                editText = (EditText) stub.findViewById(R.id.text);
                //   HorizontalScrollView container = (HorizontalScrollView) stub.findViewById(R.id.container);
                container = (LinearLayout) stub.findViewById(R.id.ll0);

                container.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                       //create bounds of all keys to check which letter is hovered
                        if (create) {
                            create = false;
                            kq = new KeyboardQWERTY(container);
                        }

                        //log touches
                        if(mEventLogger!=null){
                           // mEventLogger.writeAsync(motionEvent.getActionIndex() + ","+motionEvent.getAction() +","+motionEvent.getX() + "," +motionEvent.getY() + "," + motionEvent.getEventTime());
                        }

                       // Log.d("teste", motionEvent.toString());

                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                lastLetter = kq.getCharacter((int) motionEvent.getX(), (int) motionEvent.getY());
                                sl.sendMessage(getApplicationContext(), TO_READ, lastLetter);
                                break;
                            case MotionEvent.ACTION_MOVE:
                                String letter = kq.getCharacter((int) motionEvent.getX(), (int) motionEvent.getY());
                                if (!lastLetter.equals(letter)) {
                                    lastLetter = letter;
                                    sl.sendMessage(getApplicationContext(), TO_READ, lastLetter);
                                }
                                break;
                            case MotionEvent.ACTION_UP:
                                if (!lastLetter.equals("")) {
                                    sl.sendMessage(getApplicationContext(), TO_READ, "up");
                                    if(lastLetter.equals("espasso"))
                                        lastLetter=" ";
                                    phrase += lastLetter;
                                    editText.setText(phrase);
                                }
                                break;
                        }
                        return true;
                    }


                });
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();

    }


}
