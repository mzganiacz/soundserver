package com.zganiacz.axwave.server;

import javax.sound.sampled.*;

/**
 * Created by Dynamo on 23.11.2016.
 */
public class AudioInputDataLineBuilder {


    public static AudioInputStream buildAudioInputStream() {

        TargetDataLine targetDataLine;

        try {
            targetDataLine = AudioSystem.getTargetDataLine(getAudioFormat());

            int len = (int) (targetDataLine.getFormat().getSampleRate() * (targetDataLine.getFormat().getSampleSizeInBits() / 8) * targetDataLine.getFormat().getChannels());

            AudioInputStream audioInputStream = new AudioInputStream(targetDataLine);

            return audioInputStream;

        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static AudioFormat getAudioFormat() {
        float sampleRate = 8000.0F;
        //8000,11025,16000,22050,44100
        int sampleSizeInBits = 16;
        //8,16
        int channels = 1;
        //1,2
        boolean signed = true;
        //true,false
        boolean bigEndian = false;
        //true,false
        return new AudioFormat(
                sampleRate,
                sampleSizeInBits,
                channels,
                signed,
                bigEndian);
    }//end getAudioFormat


}
