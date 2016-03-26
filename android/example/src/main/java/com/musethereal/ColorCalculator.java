package com.musethereal;

/**
 * Created by kbrockman on 16-03-26.
 */
public class ColorCalculator {
    private byte[] currentColors = new byte[]{
            (byte) 0xFF, (byte) 0x00, (byte) 0x00,  // 0 - AF4
            (byte) 0x00, (byte) 0xFF, (byte) 0x00,  // 1 - T8
            (byte) 0x00, (byte) 0x00, (byte) 0xFF,  // 2 - O2
            (byte) 0xFF, (byte) 0xFF, (byte) 0x00,  // 3 - O1
            (byte) 0x00, (byte) 0xFF, (byte) 0xFF,  // 4 - T7
            (byte) 0xFF, (byte) 0x00, (byte) 0xFF,  // 5 - AF6
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,  // 6 - F3
            (byte) 0xFF, (byte) 0x99, (byte) 0x00,  // 7 - F8
            (byte) 0x99, (byte) 0x00, (byte) 0xFF,  // 8 - FC6
            (byte) 0x00, (byte) 0x00, (byte) 0x00,  // 9 - P8
            (byte) 0x00, (byte) 0x00, (byte) 0x00,  // 10 - FC5
            (byte) 0x00, (byte) 0x00, (byte) 0x00,  // 11 - F7
            (byte) 0x00, (byte) 0x00, (byte) 0x00  // 12 - P7
    };

    public byte[] ConvertToColors(EEGReading reading){
        return currentColors;
    }
}
