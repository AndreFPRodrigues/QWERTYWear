package entry.text.workshop.qwerty.typebraille;

import java.util.HashMap;

/**
 * Created by andre on 24-Jun-15.
 */
public class BrailleAlphabet {
    private HashMap<Integer, String> letters;

    private final int LEFT = 1;
    private final int RIGHT = 2;
    private final int DOUBLE = 3;

    private static final int LINE_ONE=1;
    public static final int LINE_TWO=10;
    public static final int LINE_THREE=1000;

    private static BrailleAlphabet alphabet;

    public static BrailleAlphabet getSharedInstance(){
        if(alphabet!=null) {
            return alphabet;
        }
        else {
            return new BrailleAlphabet();
        }
    }

    private BrailleAlphabet() {
        letters= new HashMap<Integer, String>();
        letters.put(LEFT,"A");
        letters.put(LEFT+LEFT*LINE_TWO,"B");
        letters.put(DOUBLE,"C");
        letters.put(DOUBLE+ RIGHT *LINE_TWO,"D");
        letters.put(LEFT+ RIGHT *LINE_TWO,"E");
        letters.put(DOUBLE +LEFT*LINE_TWO,"F");
        letters.put(DOUBLE +DOUBLE*LINE_TWO,"G");
        letters.put(LEFT +DOUBLE*LINE_TWO,"H");
        letters.put(RIGHT +LEFT*LINE_TWO,"I");
        letters.put(RIGHT +DOUBLE*LINE_TWO,"J");
        letters.put(LEFT +LEFT*LINE_THREE,"K");
        letters.put(LEFT +LEFT*LINE_TWO+LEFT*LINE_THREE,"L");
        letters.put(DOUBLE+LEFT*LINE_THREE,"M");
        letters.put(DOUBLE+RIGHT*LINE_TWO+LEFT*LINE_THREE,"N");
        letters.put(LEFT+RIGHT*LINE_TWO+LEFT*LINE_THREE,"O");
        letters.put(DOUBLE+LEFT*LINE_TWO+LEFT*LINE_THREE,"P");
        letters.put(DOUBLE+DOUBLE*LINE_TWO+LEFT*LINE_THREE,"Q");
        letters.put(LEFT+DOUBLE*LINE_TWO+LEFT*LINE_THREE,"R");
        letters.put(RIGHT+LEFT*LINE_TWO+LEFT*LINE_THREE,"S");
        letters.put(RIGHT+DOUBLE*LINE_TWO+LEFT*LINE_THREE,"T");
        letters.put(LEFT+DOUBLE*LINE_THREE,"U");
        letters.put(LEFT+LEFT*LINE_TWO+DOUBLE*LINE_THREE,"V");
        letters.put(DOUBLE+DOUBLE*LINE_THREE,"X");
        letters.put(LEFT+DOUBLE*LINE_TWO+RIGHT*LINE_THREE,"W");
        letters.put(DOUBLE+RIGHT*LINE_TWO+DOUBLE*LINE_THREE,"Y");
        letters.put(LEFT+RIGHT*LINE_TWO+DOUBLE*LINE_THREE,"Z");
    }

    public String getLetter(int value){
        String letter = letters.get(value);
        return letter!=null?letter:"";
    }

}
