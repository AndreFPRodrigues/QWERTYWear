package entry.text.workshop.qwerty.typebraille;

import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by andre on 24-Jun-15.
 */
public class BrailleLine {
    private final static int LEFT = 1;
    private final static int RIGHT = 2;
    private final static int DOUBLE = 3;
    public final static int EMPTY = 0;
    public final static int NEXT_CHAR = 4;
    private final static int MID_THRESHOLD = 150;
    private final static int MOVE_COUNT_THRESHOLD = 3;
    private final static int MOVE_THRESHOLD = 50;

    private final String TAG = "BrailleType";
    private int type;
    private int moveCount;
    private int xOrig;
    private int yOrig;
    private int xFinal;
    private int yFinal;

    public BrailleLine(MotionEvent event) {
        xOrig = (int) event.getX();
        yOrig = (int) event.getY();
        moveCount = 0;
        if (event.getX() < MID_THRESHOLD)
            type = LEFT;
        else
            type = RIGHT;

    }

    public void processEvent(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            type = DOUBLE;
            Log.d(TAG, "type changed");
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            moveCount++;
            xFinal = (int) event.getX();
            yFinal = (int) event.getY();
        }
    }


    //when a fling down is detected
    public void empty() {
        type = EMPTY;
    }

    public int getType() {
        if (moveCount > MOVE_COUNT_THRESHOLD && Math.max((xFinal-xOrig), (yFinal-yOrig))> MOVE_THRESHOLD) {
            if ((xFinal - xOrig) > (yFinal - yOrig)) {
                return NEXT_CHAR;
            } else {
                empty();
                return EMPTY;
            }
        }
        return type;
    }
}
