package entry.text.workshop.qwerty.typebraille;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by andre on 24-Jun-15.
 */
public class BrailleLetter {
    int value;
    int rows;
    BrailleAlphabet brailleAlphabet;

    public BrailleLetter(BrailleLine line){
       value=line.getType();
        rows=1;
        brailleAlphabet=BrailleAlphabet.getSharedInstance();
    }

    //returns true if completed letter (third row received)
    public boolean addLine(BrailleLine line){

        rows++;
        if(rows==2){
            value+=line.getType()*BrailleAlphabet.LINE_TWO;
        }else{
            value+=line.getType()*BrailleAlphabet.LINE_THREE;
            return true;
        }
        return false;
    }

    public String getLetter(){
        return brailleAlphabet.getLetter(value);
    }



}
