package entry.text.workshop.qwerty;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by andre on 03-Jun-15.
 */
public class StudyController {
    private Random generator;
    private final String TAG = "QWERTY";
    private  int phraseNumber;
    private ArrayList<String> mSentences = new ArrayList<String>();
    private final int NUMBER_TRIALS=3;
    Context c;



    public StudyController(Context c) {
        //getSetences
        mSentences = XMLHandler.getSentences(c);
        generator= new Random();
        this.c=c;
        phraseNumber=-1;
    }



    public String nextPhrase(){

        phraseNumber++;
        if(phraseNumber>NUMBER_TRIALS){

            return null;
        }

        String sentence=getRandomSetence();


        return sentence;

    }


    //get next random setence
    private String getRandomSetence(){
        int i = generator.nextInt(mSentences.size());
        String phrase = mSentences.get(i);
        mSentences.remove(i);
        return phrase;
    }

    public int getPhraseIndex() {
        return phraseNumber;
    }
}
