package entry.text.workshop.qwerty;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import storage.Logger;


/**
 * Created by Kyle Montague on 11/05/15.
 */
public class SyncActivity extends Activity {

    Button syncButton;
    TextView syncText;

    long created;
    Logger mLogger;
    static String SYNC = "SYNC";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            created = extras.getLong("created");
        }


        mLogger = new Logger(SYNC,10,created,Logger.FileFormat.csv);

        syncText = (TextView) findViewById(R.id.debugInfo);
        syncButton = (Button)findViewById(R.id.syncButton);
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLogger.writeAsync(String.valueOf(System.currentTimeMillis()));
                mLogger.flush();
                finish();
            }
        });
    }
}
