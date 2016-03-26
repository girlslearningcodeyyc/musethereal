package com.musethereal;

import com.emotiv.EEGHeadset;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by kbrockman on 16-03-23.
 */
public class EEGReading {
    public Map<String, BigDecimal[]> Channels;

    public EEGReading(){
        Channels.put("AF3", new BigDecimal[5]);
        Channels.put("AF", new BigDecimal[5]);
        Channels.put("T7", new BigDecimal[5]);
        Channels.put("Pz", new BigDecimal[5]);
        Channels.put("T8", new BigDecimal[5]);
        Channels.put("AF4", new BigDecimal[5]);
        Channels.put("O2", new BigDecimal[5]);
        Channels.put("F7", new BigDecimal[5]);
        Channels.put("P8", new BigDecimal[5]);
        Channels.put("F3", new BigDecimal[5]);
        Channels.put("FC5", new BigDecimal[5]);
        Channels.put("FC6", new BigDecimal[5]);
        Channels.put("F4", new BigDecimal[5]);
        Channels.put("P7", new BigDecimal[5]);
        Channels.put("F8", new BigDecimal[5]);
        Channels.put("O1", new BigDecimal[5]);
    }
}
