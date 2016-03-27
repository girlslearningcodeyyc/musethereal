package com.musethereal;
import java.util.HashMap;
import java.util.Random;
/**
 * Created by kbrockman on 16-03-26.
 */
public class ColorCalculator {
    private Random rando;

    public ColorCalculator (){
        rando = new Random();
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
