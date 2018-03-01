package Utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by scapista on 2/24/18.
 * +
 * + Priority:
 * + 1. build failing
 * + 2. function not working
 * + 3. nice to have but would optimize
 * + 4. nice to have no benefit
 * + 5. future state
 */

public class SplitString {


    public ArrayList<String> regexMatches(String text, String regex){
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        return null;
    }
}
