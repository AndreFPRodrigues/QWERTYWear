package entry.text.workshop.qwerty;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by andre on 03-Jun-15.
 */
public class Logging {

    private static final String INIT_FILE = "{\"Touches\":[";
    private final String END_SEQUENCE = "]},";
    private final String filepath = Environment.getExternalStorageDirectory().toString() + "/";
    private final String filepathEnd = "_io_qwerty.json";
    private final String TAG = "QWERTY";
    private ArrayList<Touch> touches;

    private String username;

    private ArrayList<Phrase> phrasesLog;
    private long endPhrase = 0;
    private long initPhrase = 0;

    public Logging(String name) {
        touches = new ArrayList<Touch>();
        username = name;
        phrasesLog = new ArrayList<Phrase>();

    }

    public void addTouch(Touch t) {
        if (touches != null) {
            touches.add(t);
            if (initPhrase == 0)
                initPhrase = System.currentTimeMillis();

            if (t.getType() == 1)
                endPhrase = System.currentTimeMillis();

        }
    }

    public void writeTouchesToFile() {
        if (touches.size() < 1) {
            return;
        }

        File file = new File(filepath + username + filepathEnd);
        boolean exists = file.exists();
        FileWriter fw;
        try {
            fw = new FileWriter(file, true);
            if (!exists) {
                fw.write(INIT_FILE);
            }
            boolean first = true;
            for (Touch t : touches) {
                if (first) {
                    fw.write(t.toJSON());
                    first = false;
                } else {
                    fw.write(" , " + t.toJSON());
                }
            }
            fw.write("]}");

            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        touches = new ArrayList<Touch>();
    }


    public void clear() {
        initPhrase = 0;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public void closeFile(Context c, int phraseNumber) {
        long totalTime = System.currentTimeMillis() - endPhrase;
        XMLHandler.saveToXml(c, phrasesLog, phraseNumber, totalTime, initPhrase, username);

        writeTouchesToFile();


    }

    public void setTargetPhrase(String phrase) {
        phrasesLog.add(new Phrase(phrase));
    }

    public void savePhrase(int index, String phrase) {
        Log.d(TAG, "SAVED" + phrase + " init:" + initPhrase + " end:" + endPhrase);
        if (index > -1) {
            phrasesLog.get(index).save(phrase, initPhrase, endPhrase);
            initPhrase = 0;
        }
    }

    public void addKeystroke(int index, String c) {
        phrasesLog.get(index).addLetter(c);

    }


}
