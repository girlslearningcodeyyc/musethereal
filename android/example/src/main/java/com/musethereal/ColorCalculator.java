package com.musethereal;

/**
 * Created by kbrockman on 16-03-26.
 */
public class ColorCalculator {
    private char[] currentColors = new char[]{
            0xFF, 0x00, 0x00,  // 0 - AF4
            0x00, 0xFF, 0x00,  // 1 - T8
            0x00, 0x00, 0xFF,  // 2 - O2
            0xFF, 0xFF, 0x00,  // 3 - O1
            0x00, 0xFF, 0xFF,  // 4 - T7
            0xFF, 0x00, 0xFF,  // 5 - AF6
            0xFF, 0xFF, 0xFF,  // 6 - F3
            0xFF, 0x77, 0x00,  // 7 - F8
            0x00, 0x00, 0x00,  // 8 - FC6
            0x00, 0x00, 0x00,  // 9 - P8
            0x00, 0x00, 0x00,  // 10 - FC5
            0x00, 0x00, 0x00,  // 11 - F7
            0x00, 0x00, 0x00,  // 12 - P7
    };

    public char[] ConvertToColors(EEGReading reading){
        return currentColors;
    }
}
