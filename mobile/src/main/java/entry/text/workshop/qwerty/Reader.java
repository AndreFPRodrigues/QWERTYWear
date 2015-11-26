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

    private long lastWrite;
    private final long FLUSH_THRESHOLD=500;

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
            int type = TextToSpeech.QUEUE_FLUSH;
            if(lastWrite-System.currentTimeMillis()>FLUSH_THRESHOLD)
                type=TextToSpeech.QUEUE_ADD;
            ttobj.speak(message,type , null);
            lastLetter=message;
            return lastLetter;
    }

    public String writeLetter(){
            lastWrite = System.currentTimeMillis();
            ttobj.speak(lastLetter, TextToSpeech.QUEUE_FLUSH, null);
            if(lastLetter.equals("espasso"))
                lastLetter=" ";
            if(lastLetter.equals("apagar")){
                lastLetter="<<";
                if(phrase.length()>0) {
                    phrase = phrase.substring(0, phrase.length() - 1);
                }
            }else {
                phrase += lastLetter;
            }
            return lastLetter;

    }
    public String getPhrase(){
        return phrase;

    }

    public void read(String toRead) {
        int type = TextToSpeech.QUEUE_FLUSH;

        if(lastWrite-System.currentTimeMillis()<FLUSH_THRESHOLD)
                type=TextToSpeech.QUEUE_ADD;
        ttobj.speak(toRead, type, null);

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
