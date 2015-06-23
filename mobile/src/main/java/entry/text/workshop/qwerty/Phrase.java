package entry.text.workshop.qwerty;

import java.util.ArrayList;

/**
 * Created by andre on 20-Jan-15.
 */


public class Phrase {
    private ArrayList<CharEntered> chars;
    private String result_phrase;
    private String target_phrase;
    private long init;
    private long end;



    public Phrase(String target_phrase){
        this.target_phrase=target_phrase;
        result_phrase ="";
        chars = new ArrayList<CharEntered>();
    }

    public void addLetter(String s){
        chars.add(new CharEntered(s, System.currentTimeMillis()));
    }

    public void save(String phrase, long init, long end){
        this.result_phrase =phrase;
        this.init=init;
        this.end=end;
    }

    public long getInit() {
        return init;
    }

    public long getEnd() {
        return end;
    }

    public String getResult() {
        return result_phrase;
    }


    public ArrayList<CharEntered> getChars() {
        return chars;
    }

    public String getTarget() {
        return target_phrase;
    }

    public long getTotal() {
        return (end-init);
    }

    public class CharEntered {
        String c;
        long timestamp;


        public long getTimestamp() {
            return timestamp;
        }

        public String getC() {
            return c;
        }

        public CharEntered(String c, long timestamp){
            this.c=c;
            this.timestamp=timestamp;
        }
    }
}

