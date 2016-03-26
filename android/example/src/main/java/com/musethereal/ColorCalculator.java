package com.musethereal;

/**
 * Created by kbrockman on 16-03-26.
 */
public class ColorCalculator {
    private char[] currentColors = new char[]{
            (char)0xFFFFFF,  // 0 - AF4
            (char)0xFFFFFF,  // 1 - T8
            (char)0xFFFFFF,  // 2 - O2
            (char)0xFFFFFF,  // 3 - O1
            (char)0xFFFFFF,  // 4 - T7
            (char)0xFFFFFF,  // 5 - AF6
            (char)0xFFFFFF,  // 6 - F3
            (char)0xFFFFFF,  // 7 - F8
            (char)0xFFFFFF,  // 8 - FC6
            (char)0xFFFFFF,  // 9 - P8
            (char)0xFFFFFF,  // 10 - FC5
            (char)0xFFFFFF,  // 11 - F7
            (char)0xFFFFFF,  // 12 - P7
    };

    public char[] ConvertToColors(EEGReading reading){
        return currentColors;
    }
}
