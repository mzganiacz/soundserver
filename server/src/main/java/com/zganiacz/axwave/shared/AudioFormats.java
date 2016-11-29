package com.zganiacz.axwave.shared;

import javax.sound.sampled.AudioFormat;
import java.util.LinkedHashMap;

/**
 * Created by Dynamo on 25.11.2016.
 */
public class AudioFormats {
    public static LinkedHashMap<Short, Format> FORMATS = new LinkedHashMap<Short, Format>(3);

    static {
        FORMATS.put((short) 1, new Format(new AudioFormat(8000.0F, 8, 1, true, false), (short) 3, "S8kHz16bitMono"));
        FORMATS.put((short) 2, new Format(new AudioFormat(8000.0F, 16, 1, true, false), (short) 3, "S8kHz16bitMono"));
        FORMATS.put((short) 3, new Format(new AudioFormat(22050.0F, 16, 1, true, false), (short) 2, "S22kHz16bitMono"));
        FORMATS.put((short) 4, new Format(new AudioFormat(44100.0F, 16, 2, true, false), (short) 1, "S44kHz16bitStereo"));
    }

    public static class Format {
        private final AudioFormat audioFormat;
        private final short code;
        private final String name;

        public Format(AudioFormat audioFormat, short code, String name) {
            this.audioFormat = audioFormat;
            this.code = code;
            this.name = name;
        }

        public AudioFormat getAudioFormat() {
            return audioFormat;
        }

        public short getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }


}
