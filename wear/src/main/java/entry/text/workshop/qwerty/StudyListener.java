package entry.text.workshop.qwerty;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.File;

/**
 * Created by kyle montague on 11/05/15.
 */
public class StudyListener extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public static final int MESSAGE_START_SENSORS = 1;
    public static final int MESSAGE_STOP_SENSORS = 2;
    public static final int MESSAGE_SEND_DATA = 3;
    public static final int MESSAGE_REMOVE_DATA = 4;
    public static final int MESSAGE_STOP_COMPLETELY = 5;
    public static final int MESSAGE_START = 0;
    public static final int MESSAGE_SEND_ALL_DATA = 9;




    public static String ACTION_START_SYNC = "TRACES.MSG.SYNC_START";
    public static String ACTION_SYNC_COMPLETE = "TRACES.MSG.SYNC_COMPLETE";
    public static String ACTION_START_LOGGER = "TRACES.MSG.LOGGER_START";
    public static String ACTION_STOP_LOGGER = "TRACES.MSG.LOGGER_STOP";

    public static String ACTION_START_SENSORS = "TRACES.MSG.SENSORS_START";
    public static String ACTION_STOP_SENSORS = "TRACES.MSG.SENSORS_STOP";

    public static String ACTION_SEND_FILES = "TRACES.MSG.SEND_FILES";
    public static String ACTION_REMOVE_FILES = "TRACES.MSG.REMOVE_FILES";


    public static String ACTION_ZIPPED_FILES = "/logfile";
    public static String ACTION_GET_ALL_FILES = "TRACES.MSG.SEND_ALL_FILES";



    private GoogleApiClient mGoogleApiClient;
    private String TAG = "STUDYLISTENER";

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(StudyListener.this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) { }



    @Override
    public void onMessageReceived(MessageEvent messageEvent){
        final String message = new String(messageEvent.getData());
        String[]values = message.split(",");
        long created =0;
        if(values.length >0 && values[0].length() > 1)
            created = Long.valueOf(values[0]);

        Intent i = new Intent(StudyListener.this,MainActivity.class);
        i.putExtra("created",created);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_NO_HISTORY);

        if(messageEvent.getPath().contains(ACTION_SEND_FILES)){
            Toast.makeText(StudyListener.this,"PREPARING TO SEND...",Toast.LENGTH_SHORT).show();
            i.putExtra("message",MESSAGE_SEND_DATA);
            startActivity(i);
        }else if(messageEvent.getPath().contains(ACTION_GET_ALL_FILES)){
            Toast.makeText(StudyListener.this,"PREPARING TO SEND ALL DATA...",Toast.LENGTH_SHORT).show();
            i.putExtra("message",MESSAGE_SEND_ALL_DATA);
            startActivity(i);
        }else if(messageEvent.getPath().contains(ACTION_REMOVE_FILES)) {
            //todo remove the files containing the timestamp <timestamp> in their name.
        }else if(messageEvent.getPath().contains(ACTION_START_LOGGER) || messageEvent.getPath().contains(ACTION_SYNC_COMPLETE)) {
            Toast.makeText(StudyListener.this,"STARTING...",Toast.LENGTH_SHORT).show();
            i = new Intent(StudyListener.this,MainActivity.class);
            i.putExtra("message",MESSAGE_START);
            i.putExtra("created",created);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(i);
        }else if(messageEvent.getPath().contains(ACTION_STOP_LOGGER)) {
            Toast.makeText(StudyListener.this,"STOPPING...",Toast.LENGTH_SHORT).show();
            i.putExtra("message",MESSAGE_STOP_COMPLETELY);
            startActivity(i);
        }else if(messageEvent.getPath().contains(ACTION_START_SENSORS)) {
            Toast.makeText(StudyListener.this,"SENSOR STARTING...",Toast.LENGTH_SHORT).show();
            i.putExtra("message",MESSAGE_START_SENSORS);
            startActivity(i);
        }else if(messageEvent.getPath().contains(ACTION_STOP_SENSORS)) {
            Toast.makeText(StudyListener.this,"SENSOR STOPPING...",Toast.LENGTH_SHORT).show();
            i.putExtra("message",MESSAGE_STOP_SENSORS);
            startActivity(i);
        }else if(messageEvent.getPath().contains(ACTION_START_SYNC)) {
            i = new Intent(StudyListener.this,SyncActivity.class);
            i.putExtra("created",created);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(i);
        }else{
            super.onMessageReceived(messageEvent);
        }
    }

    public void sendMessage(Context context, final String key, final String message){
        if(mGoogleApiClient == null){
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        }

        mGoogleApiClient.connect();

        if (mGoogleApiClient.isConnected()) {
            Log.v(TAG, "Is Connected");

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
                            Log.v(TAG, "error");
                        } else {
                            Log.v(TAG, "success!! sent to: " + node.getDisplayName());
                        }
                    }
                }
            }).start();
        } else {
            Log.v(TAG, "Is NOT Connected");
        }
    }

    public void sendLogfile(Context context, String filepath, String folder, long created){
        if(mGoogleApiClient == null){
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        }
            File f = new File(filepath);
            if (!f.exists())
                return;

            Toast.makeText(context,f.getPath(),Toast.LENGTH_LONG).show();
            Asset asset = Asset.createFromUri(Uri.fromFile(f));
            PutDataMapRequest dataMap = PutDataMapRequest.create(ACTION_ZIPPED_FILES);
            dataMap.getDataMap().putAsset("file", asset);
            dataMap.getDataMap().putString("name", f.getName());
            dataMap.getDataMap().putString("folder",folder);
            dataMap.getDataMap().putLong("created",created);
            dataMap.getDataMap().putLong("timestamp", System.currentTimeMillis());
            PutDataRequest request = dataMap.asPutDataRequest();
            PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request);
            Toast.makeText(context,pendingResult.toString(),Toast.LENGTH_LONG).show();
    }
}
