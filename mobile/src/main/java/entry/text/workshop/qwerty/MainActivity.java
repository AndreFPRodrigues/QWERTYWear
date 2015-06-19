package entry.text.workshop.qwerty;

import android.content.IntentSender;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Locale;


public class MainActivity extends ActionBarActivity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks, NodeApi.NodeListener, GoogleApiClient.OnConnectionFailedListener {
    private Handler mHandler;
    private TextView tv;
    private TextView tv_phrase;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    private final String TO_READ="toRead";
    private TextToSpeech ttobj;
    private String phrase;
    private String lastLetter;
    /**
     * Request code for launching the Intent to resolve Google Play services errors.
     */
    private static final int REQUEST_RESOLVE_ERROR = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.textv);
        tv_phrase = (TextView) findViewById(R.id.phrase);
        phrase="";
        lastLetter="";
        mHandler = new Handler();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        ttobj=new TextToSpeech(getApplicationContext(),
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR){
                            ttobj.setLanguage(Locale.UK);
                        }
                    }
                });

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
        final String message = new String(messageEvent.getData());
        if(messageEvent.getPath().contains(TO_READ)) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    if(message.equals("up")){
                        ttobj.speak(lastLetter, TextToSpeech.QUEUE_FLUSH, null);
                        tv.setText(lastLetter);
                        if(lastLetter.equals("espasso"))
                            lastLetter=" ";
                        phrase+=lastLetter;
                        tv_phrase.setText(phrase);
                    }else{
                        ttobj.speak(message, TextToSpeech.QUEUE_FLUSH, null);
                        tv.setText(message);
                        lastLetter=message;
                    }

                }
            });
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
