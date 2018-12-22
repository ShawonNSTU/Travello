package com.example.shawon.travelbd.Utils;

/**
 * Created by SHAWON on 12/22/2018.
 */

public class StringManipulation {

    public static String getTags(String caption) {
        if (caption.indexOf('#') >= 0) {
            StringBuilder stringBuilder = new StringBuilder();
            char[] charArray = caption.toCharArray();
            boolean found = false;
            for (char ch : charArray){
                if (ch == ' ') found = false;
                else if (ch == '\n') found = false;
                else if (ch == '#'){
                    found = true;
                    stringBuilder.append(ch);
                }
                else if(found) stringBuilder.append(ch);
            }
            String s = stringBuilder.toString().replace("##", "#").replace("#", ",#");
            return s.substring(1,s.length());
        }
        else{
            return "No Tags";
        }
    }
}