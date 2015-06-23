package entry.text.workshop.qwerty;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by andre on 23-Jun-15.
 */
public class Reader {
    private TextToSpeech ttobj;
    private String phrase;
    private String lastLetter;

    public Reader(Context c){
        phrase="";
        lastLetter="";
        ttobj=new TextToSpeech(c,
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR){
                            ttobj.setLanguage(Locale.UK);
                        }
                    }
                });
    }

    public String getLetter(String message){
        if(message.equals("up")){
            ttobj.speak(lastLetter, TextToSpeech.QUEUE_FLUSH, null);
            if(lastLetter.equals("espasso"))
                lastLetter=" ";
            phrase+=lastLetter;
            return lastLetter;
        }else{
            ttobj.speak(message, TextToSpeech.QUEUE_FLUSH, null);
            lastLetter=message;
            return lastLetter;
        }
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
}
