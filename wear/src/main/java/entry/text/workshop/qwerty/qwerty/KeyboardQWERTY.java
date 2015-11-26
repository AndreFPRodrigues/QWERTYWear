package entry.text.workshop.qwerty.qwerty;

import android.graphics.Rect;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import entry.text.workshop.qwerty.R;

/**
 * Created by andre on 19-May-15.
 */
public class KeyboardQWERTY {

    private final String[] characters = new String[]{"q", "w", "e", "r",
            "t", "y", "u", "i", "o", "p", "a", "s", "d", "f", "g", "h",
            "j", "k", "l", "z", "x", "c", "v", "b", "n", "m", "espasso"};
    private final int firstRow = 10;
    private final int secondRow = 19;
    private final int thirdRow = 27;
    private ArrayList<Rect> bounds;
    private int lastRead;
    private Rect lastBounds;
    private int invisibleBounds;
    private boolean adaptBounds;

    public KeyboardQWERTY(LinearLayout parent, boolean adaptBounds) {
        adaptBounds = adaptBounds;
        int rows = parent.getChildCount();
        invisibleBounds = parent.findViewById(R.id.Button_A).getWidth();
        invisibleBounds += (invisibleBounds / 2);
        bounds = new ArrayList<Rect>();
        for (int i = 0; i < rows; i++) {
            LinearLayout row = ((LinearLayout) parent.getChildAt(i));
            int columns = row.getChildCount();

            for (int j = 0; j < columns; j++) {
                if (j + 1 < columns) {
                    addBounds((TextView) row.getChildAt(j), j);
                } else {
                    //last to the right
                    addBounds((TextView) row.getChildAt(j), -1);

                }
            }
        }
    }

    private void addBounds(TextView childAt, int index) {
        int[] position = new int[2];
        if (childAt != null) {
            childAt.getLocationOnScreen(position);
            int width = childAt.getWidth();
            int height = childAt.getHeight();
            int right = position[0] + width;
            int left = position[0];
            //start touch from x=0
            /*if(index==0)
                left=0;
            else if(index==-1){
                //touch limit to the right off bounds
                right=305;
            }*/
            Rect bound = new Rect(left, position[1], right, position[1] + height);
            //childAt.getLocalVisibleRect(bound);
            bounds.add(bound);
            //  Log.d("teste", bound.toString());
            // Log.d("teste", childAt.getText() + " :" + width + " " + height + " " + position[0] + " " + position[1]);
        }
    }


    public String getCharacter(int x, int y) {
        //Log.d("teste", "x:" + x + " y:" + y);

        //verify with adapt
        if (lastBounds != null && adaptBounds) {
            if (!crossInvisibleBounds(x, y, lastBounds, lastRead))
                return characters[lastRead];
            else {
                int row = getRow(lastRead);
                for (int i = 0; i < bounds.size(); i++) {
                    if (getRow(i) == row && !crossInvisibleBounds(x, y, bounds.get(i), i)) {
                        lastRead = i;
                        lastBounds = bounds.get(i);
                        return characters[i];
                    }
                }
            }

        }

        //verify without any restriction
        for (int i = 0; i < bounds.size(); i++) {
            if (bounds.get(i).contains(x, y)) {
                // Log.d("teste", "x:" + x + " y:" + y + " char:" + characters[i]);
                lastRead = i;
                lastBounds = bounds.get(i);
                return characters[i];
            }
        }

        return "";


    }

    private boolean crossInvisibleBounds(int x, int y, Rect bounds, int index) {
        if (Math.abs(bounds.centerX() - x) > invisibleBounds)
            return true;
        else {
            if (getRow(index) > 0 && getRow(index) <3  && Math.abs(bounds.centerY() - y) > invisibleBounds)
                return true;
        }
        return false;
    }

    private int getRow(int index) {
        if (index < firstRow)
            return 0;
        if (index < secondRow)
            return 1;
        if (index < thirdRow)
            return 2;
        return 3;
    }

    public void clearBounds() {
        lastBounds = null;
    }
}
