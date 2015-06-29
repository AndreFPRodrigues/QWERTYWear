package entry.text.workshop.qwerty;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by andre on 23-Jun-15.
 */
public class Reader {
    private TextToSpeech ttobj;
    private String phrase;
    private String lastLetter;

    //braille line codes
    private final static int LEFT = 1;
    private final static int RIGHT = 2;
    private final static int DOUBLE = 3;
    private final static int EMPTY = 0;
    private final static int NEXT_CHAR = 4;

    public Reader(Context c){
        phrase="";
        lastLetter="";
        ttobj=new TextToSpeech(c,
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR){
                            ttobj.setLanguage(Locale.getDefault());
                        }
                    }
                });
    }

    public String getLetter(String message){
            ttobj.speak(message, TextToSpeech.QUEUE_FLUSH, null);
            lastLetter=message;
            return lastLetter;
    }

    public String writeLetter(){
            ttobj.speak(lastLetter, TextToSpeech.QUEUE_FLUSH, null);
            if(lastLetter.equals("espasso"))
                lastLetter=" ";
            phrase+=lastLetter;
            return lastLetter;

    }
    public String getPhrase(){
        return phrase;

    }

    public void read(String toRead) {
        ttobj.speak(toRead, TextToSpeech.QUEUE_FLUSH, null);

    }

    public String lastRead() {
        return lastLetter;
    }

    public void clear(){
        phrase="";
        lastLetter="";
    }

    public String decodeLog(String message) {
        int type =Integer.parseInt(message);
        switch (type){
            case LEFT:
                return "LEFT";
            case RIGHT:
               return "RIGHT";
            case DOUBLE:
                return "DOUBLE";
            case EMPTY:
                return "EMPTY";
            case NEXT_CHAR:
                return "NEXT_CHAR";
        }
        return null;
    }
}
