package entry.text.workshop.qwerty;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;


public class MainActivity extends ActionBarActivity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks, NodeApi.NodeListener, GoogleApiClient.OnConnectionFailedListener {
    public static final String IOLOG = "IOLog";
    public static final String TO_READ = "toRead";
    public static final String TO_WRITE = "toWrite";
    public static final String INIT = "init";
    public static final String LOG = "debug";
    private static final long TIMEOUT_MS = 10000;
    private static final String TAG = "QWERTY";
    /**
     * Request code for launching the Intent to resolve Google Play services errors.
     */
    private static final int REQUEST_RESOLVE_ERROR = 1000;
    public static String ACTION_ZIPPED_FILES = "/logfile";
    private Handler mHandler;
    private TextView letter;
    private TextView phrase;
    private TextView target;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    private String username;
    private Reader reader;
    private StudyController stdc;
    private Logging log;
    int keyboard;
    boolean adapt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        letter = (TextView) findViewById(R.id.textv);
        phrase = (TextView) findViewById(R.id.trainingPhrase);
        reader = new Reader(this);
        stdc = new StudyController(this);
        Intent intent = getIntent();
        username = intent.getAction();
        mHandler = new Handler();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        if (!mResolvingError) {
            // Wearable.DataApi.removeListener(mGoogleApiClient, this);
            Wearable.MessageApi.removeListener(mGoogleApiClient, this);
            Wearable.NodeApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override //ConnectionCallbacks
    public void onConnected(Bundle connectionHint) {
        Log.d("teste", "CONECET  MESSAGE");

        mResolvingError = false;
        //   Wearable.DataApi.addListener(mGoogleApiClient, this);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        Wearable.NodeApi.addListener(mGoogleApiClient, this);
    }

    @Override //OnConnectionFailedListener
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            mResolvingError = false;
            //Wearable.DataApi.removeListener(mGoogleApiClient, this);
            Wearable.MessageApi.removeListener(mGoogleApiClient, this);
            Wearable.NodeApi.removeListener(mGoogleApiClient, this);
        }
    }

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        final long time = System.currentTimeMillis();
        final String message = new String(messageEvent.getData());

        if (messageEvent.getPath().contains(INIT)) {
            String []s= message.split(",");
             keyboard = Integer.parseInt(s[0]);
             adapt = Boolean.parseBoolean(s[1]);

        } else {
            if (messageEvent.getPath().contains(TO_READ)) {
                final String read = reader.getLetter(message);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        letter.setText( read );

                    }
                });
            } else {
                if (messageEvent.getPath().contains(IOLOG)) {
                    if (log != null) {
                        Touch t = new Touch(message,time);
                        log.addTouch(t);
                    }
                } else {
                    if (messageEvent.getPath().contains(TO_WRITE)) {
                        final String written = reader.getLetter(message);

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                letter.setText(written );
                                write();
                            }
                        });
                    } else {
                        if (messageEvent.getPath().contains(LOG)) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    letter.setText(reader.decodeLog(message));
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    private void write() {
        reader.writeLetter();
        phrase.setText(reader.getPhrase());
        if (log != null)
            log.addKeystroke(stdc.getPhraseIndex(), reader.lastRead());
    }

    public void sendMessage(Context context, final String key, final String message) {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        }

        mGoogleApiClient.connect();

        if (mGoogleApiClient.isConnected()) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                    for (Node node : nodes.getNodes()) {
                        MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                                mGoogleApiClient,
                                node.getId(),
                                key,
                                message.getBytes()).await();
                        if (!result.getStatus().isSuccess()) {
                            Log.v(TAG, "errorr");
                        } else {
                            Log.v(TAG, "success!! sent to: " + node.getDisplayName());
                        }
                    }
                }
            }).start();
        } else {
        }
    }

    private void startTraining() {
        setContentView(R.layout.study_main);
        letter = (TextView) findViewById(R.id.letter);
        phrase = (TextView) findViewById(R.id.phrase);
        target = (TextView) findViewById(R.id.target);

        log = new Logging(username);
        log.init(keyboard, adapt);

        next(null);
        reader.clear();

    }

    public void start(View v) {
        startTraining();
    }

    public void next(View v) {
        int index = stdc.getPhraseIndex();
        final String sentence = stdc.nextPhrase();
        if (sentence != null) {
            reader.read(sentence);
            log.savePhrase(index, phrase.getText().toString());
            log.setTargetPhrase(sentence);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    target.setText(sentence);
                    phrase.setText("");
                    reader.clear();

                }
            });
            //clear(null);
        } else {
            if (index > 0) {
                log.savePhrase(index, phrase.getText().toString());
                log.closeFile(getApplicationContext(), stdc.getPhraseIndex());
                finish();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onPeerConnected(Node node) {

    }

    @Override
    public void onPeerDisconnected(Node node) {

    }


}
