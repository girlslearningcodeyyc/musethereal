package com.musethereal;
import com.emotiv.insight.IEdk;

import java.util.HashMap;
import java.util.Random;
/**
 * Created by kbrockman on 16-03-26.
 */
public class ColorCalculator {
    private Random rando;
    private HashMap<String, IEdk.IEE_DataChannel_t> channelList;

    public ColorCalculator (HashMap<String, IEdk.IEE_DataChannel_t> ch){
        rando = new Random();
        channelList = ch;
    }

    public String ConvertToColors(HashMap<String, double[]> reading){


        return EmptyString();
    }

    public String EmptyString(){
        return "nnnnnnnnnnnnn";
    }

    public String Random() {
        String allcolors = "roygbcvpw";
        String s = "";

        for(int i = 0; i < 13 ; i++) {
            s = s + allcolors.charAt(rando.nextInt(allcolors.length()));
        }

        return s;
    }
}
