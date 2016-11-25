package com.zganiacz.axwave.server;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Arrays;

public class BufferedInputStreamDemo {
    public static void main(String[] args) throws Exception {

        InputStream iStream = null;
        BufferedInputStream bis = null;

        try {

            // read from file c:/test.txt to input stream
            iStream = BufferedInputStreamDemo.class.getResourceAsStream("/test.txt");

            // input stream converted to buffered input stream
            bis = new BufferedInputStream(iStream);
            byte[] b = new byte[3];
            // read and print characters one by one
            bis.read(b);
            System.out.println(Arrays.toString(b));
            bis.mark(0);
            bis.read(b);
            System.out.println(Arrays.toString(b));
            bis.reset();
            bis.read(b);
            System.out.println(Arrays.toString(b));
            bis.read(b);
            System.out.println(Arrays.toString(b));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            // releases any system resources associated with the stream
            if (iStream != null)
                iStream.close();
            if (bis != null)
                bis.close();
        }
    }
}
