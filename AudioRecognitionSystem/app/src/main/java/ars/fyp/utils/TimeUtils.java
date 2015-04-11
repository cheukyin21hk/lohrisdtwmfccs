package ars.fyp.utils;

/**
 * Created by lohris on 11/4/15.
 */
public class TimeUtils {
    //add leading function
    public static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
}
