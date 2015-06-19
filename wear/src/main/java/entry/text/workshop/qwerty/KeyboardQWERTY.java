package entry.text.workshop.qwerty;

import android.graphics.Rect;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by andre on 19-May-15.
 */
public class KeyboardQWERTY {

    private final String[] characters = new String[]{"q", "w", "e", "r",
            "t", "y", "u", "i", "o", "p", "a", "s", "d", "f", "g", "h",
            "j", "k", "l", "z", "x", "c", "v", "b", "n", "m", "espasso"};
    private ArrayList<Rect> bounds;

    public KeyboardQWERTY(LinearLayout parent) {
        int rows = parent.getChildCount();
        bounds = new ArrayList<Rect>();
        for (int i = 0; i < rows; i++) {
            LinearLayout row = ((LinearLayout) parent.getChildAt(i));
            int columns = row.getChildCount();

            for (int j = 0; j < columns; j++) {
                if(j+1<columns) {
                    addBounds((TextView) row.getChildAt(j), j);
                }else{
                    //last to the right
                    addBounds((TextView) row.getChildAt(j), -1);

                }
            }
        }
    }

    private void addBounds(TextView childAt, int index) {
        int [] position = new int [2];
        if(childAt!=null) {
            childAt.getLocationOnScreen(position);
            int width = childAt.getWidth();
            int height = childAt.getHeight();
            int right=position[0] + width;
            int left= position[0];
            //start touch from x=0
            if(index==0)
                left=0;
            else if(index==-1){
                //touch limit to the right off bounds
                right=305;
            }
            Rect bound = new Rect(left,position[1]-height ,right ,  position[1] );
            //childAt.getLocalVisibleRect(bound);
            bounds.add(bound);
            //Log.d("teste", childAt.getText() + " :" + width + " " + height + " " + position[0] + " " + position[1]);
        }
    }

    public String getCharacter(int x, int y){
       // Log.d("teste", "x:" + x + " y:" + y);
        //x=x+10;
        if(x<0)
            x=1;
        for(int i=0; i< bounds.size();i++ ){
            if(bounds.get(i).contains(x, y)) {
                return characters[i];
            }
        }
        return "";
    }
}
