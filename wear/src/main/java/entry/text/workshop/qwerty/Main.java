package entry.text.workshop.qwerty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.ToggleButton;

import entry.text.workshop.qwerty.qwerty.QWERTYMain;
import entry.text.workshop.qwerty.typebraille.TypeInBraille;


public class Main extends Activity {
    private NumberPicker np;
    private ToggleButton tb;
    private TextView tv;

    private GestureDetectorCompat mDetector;
    private int valueQWERTY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        valueQWERTY=-1;
        tb = (ToggleButton) findViewById(R.id.toggleButton);
        tv = (TextView) findViewById(R.id.tv1);

        np = (NumberPicker) findViewById(R.id.numberPicker);
        np.setMinValue(0);
        np.setMaxValue(3);
        np.setDisplayedValues(new String[]{"Full", "Large", "Medium", "Small"});
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

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
    public boolean onTouchEvent(MotionEvent event) {

        this.mDetector.onTouchEvent(event);
        return true;
    }
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";


        @Override
        public boolean onDoubleTap (MotionEvent event1) {
            if(valueQWERTY==-1) {
                qwerty(null);
            }else{
                Intent i = new Intent(getApplicationContext(), QWERTYMain.class);
                i.putExtra("keyboard",valueQWERTY);
                i.putExtra("adapt",tb.isChecked());
                startActivity(i);
                finish();
            }
            return true;
        }
    }
    public void typeInBraille(View v) {
        Intent i = new Intent(this, TypeInBraille.class);
        startActivity(i);
        finish();
    }

    public void toggle(View v) {
        tv.setText(System.currentTimeMillis() + "");
    }

    public void qwerty(View v) {
        valueQWERTY = np.getValue();
        np.setVisibility(View.GONE);
        tb.setVisibility(View.VISIBLE);
      //  tv.setVisibility(View.VISIBLE);

        /*Intent i = new Intent(this, QWERTYMain.class);
        i.setAction(valueQWERTY +"");
        startActivity(i);
        finish();*/
    }
}
