package entry.text.workshop.qwerty;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Created by andre on 20-Jan-15.
 */
public class XMLHandler {


    //SENTENCES
    public static ArrayList<String> getSentences(Context c) {

        ArrayList<String> sentences = new ArrayList<String>();

        try {
            Resources res = c.getResources();
            XmlResourceParser xpp = res.getXml(R.xml.sentences);
            xpp.next();
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("Sentence")) {
                        xpp.next();
                        sentences.add(xpp.getText());
                    }
                }
                eventType = xpp.next();
            }

            //Collections.shuffle(sentences);
            return sentences;
        } catch (Exception e) {
            return null;
        }
    }

    public static void saveToXml(Context context, ArrayList<Phrase> phrasesLog, int numberOfPhrases, long totalTime, long startTime, String username) {
        writeToFile(writeXml(phrasesLog, numberOfPhrases, totalTime, startTime), username+"_tePhrases");
        writeToFile(writeXmlKey(phrasesLog), username+"teKeystrokes" );
    }
    private static String writeXmlKey(ArrayList<Phrase> phrasesLog){

        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "keystrokes");
            int i = -1;
            for (Phrase phrase : phrasesLog) {
                i++;
                serializer.startTag("", "phrase");
                serializer.attribute("", "step", i + "");
                ArrayList<Phrase.CharEntered> chars = phrase.getChars();
                for (Phrase.CharEntered letter : chars) {

                    serializer.startTag("", "keystroke");
                    serializer.text(letter.getC());
                    serializer.endTag("", "keystroke");
                    serializer.startTag("", "timestamp");
                    serializer.text(letter.getTimestamp() + "");
                    serializer.endTag("", "timestamp");

                }
                serializer.endTag("", "phrase");

            }
            serializer.endTag("", "keystrokes");
            serializer.endDocument();
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static String writeXml(ArrayList<Phrase> phrasesLog, int numberOfPhrases, long totalTime, long startTime) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "task");
            serializer.attribute("", "totalTime", ""+totalTime);
            serializer.attribute("", "numberOfPhrases", ""+numberOfPhrases);
            serializer.attribute("", "init_timestamp", startTime + "");
            serializer.startTag("", "phrases");
            int i = -1;
            for (Phrase phrase : phrasesLog) {
                i++;
                serializer.startTag("", "phrase");
                serializer.attribute("", "step", i + "");

                serializer.startTag("", "targetPhrase");
                serializer.text(phrase.getTarget());
                serializer.endTag("", "targetPhrase");
                serializer.startTag("", "result");
                serializer.text(phrase.getResult());
                serializer.endTag("", "result");
                serializer.startTag("", "totalTime");
                serializer.text(""+phrase.getTotal());
                serializer.endTag("", "totalTime");
                serializer.startTag("", "timestampInit");
                serializer.text(""+phrase.getInit());
                serializer.endTag("", "timestampInit");
                serializer.startTag("", "timestampEnd");
                serializer.text(""+phrase.getEnd());
                serializer.endTag("", "timestampEnd");
                serializer.endTag("", "phrase");
            }

            serializer.endTag("", "phrases");




            serializer.endTag("", "task");

            serializer.endDocument();
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeToFile(String data, String fileType)  {
        String root = Environment.getExternalStorageDirectory().toString();
        String filename = root + "/textEntry/"+fileType+"_"+ System.currentTimeMillis() +".xml";

        try {
            File myDir = new File(filename);
            myDir.getParentFile().mkdirs();
            myDir.createNewFile();
            FileOutputStream fos;
            fos = new FileOutputStream(myDir);

            PrintWriter pw = new PrintWriter(fos);
            pw.write(data);
            pw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
