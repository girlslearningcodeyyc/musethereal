package com.musethereal;
import com.emotiv.insight.IEdk;

import java.util.HashMap;
import java.util.Random;

/*
LED to contact legend
1  - AF4
2  - T8
3  - O2
4  - O1
5  - T7
6  - AF3
7  - F3
8  - F8
9  - FC6
10 - P8
11 - FC5
12 - F7
13 - P7
 */

/**
 * Created by kbrockman on 16-03-26.
 */
public class ColorCalculator {
    private Random rando;
    private IEdk.IEE_DataChannel_t [] channelList;

    public ColorCalculator (IEdk.IEE_DataChannel_t [] ch){
        rando = new Random();
        channelList = ch;
    }

    public String ConvertToColors(double[][] reading){
        //Logic here is essentially...grab the channel with the highest intensity and show the color associated with it below

        /*
        0 - Theta
        1 - Alpha
        2 - Low beta
        3 - High beta
        4 - Gamma
        */

        char[] colormap = new char[5];
        colormap[0] = 'v'; //violet
        colormap[1] = 'b'; //blue
        colormap[2] = 'g'; //green
        colormap[3] = 'y'; //yellow
        colormap[4] = 'r'; //red

        String returnString = "";

        for (int i = 0; i < channelList.length ; i++){
            int dominantChannel = 0;
            for (int j = 1; j < 5; j++){
                if (reading[i][j] > reading[i][j-1])
                    dominantChannel = j;
            }
            returnString = returnString + colormap[dominantChannel];
        }

        return returnString;
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
