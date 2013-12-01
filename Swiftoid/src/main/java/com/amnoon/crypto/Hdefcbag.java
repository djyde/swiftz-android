package com.amnoon.crypto;

/**
 * "crypto 3848"
 */

public class Hdefcbag {

    public static byte[] encrypt(byte[] input) {
        byte[] output = new byte[input.length];

        for (int i = 0; i < input.length; i++) {
            output[i] = (byte) (
                    (input[i] & 0x80) >> 6 |
                    (input[i] & 0x40) >> 4 |
                    (input[i] & 0x20) >> 2 |
                    (input[i] & 0x10) << 2 |
                    (input[i] & 0x08) << 2 |
                    (input[i] & 0x04) << 2 |
                    (input[i] & 0x02) >> 1 |
                    (input[i] & 0x01) << 7 );
        }

        return output;
    }

    public static byte[] decrypt(byte[] input) {
        byte[] output = new byte[input.length];

        for (int i = 0; i < input.length; i++) {
            output[i] = (byte) (
                    (input[i] & 0x80) >> 7 |
                    (input[i] & 0x40) >> 2 |
                    (input[i] & 0x20) >> 2 |
                    (input[i] & 0x10) >> 2 |
                    (input[i] & 0x08) << 2 |
                    (input[i] & 0x04) << 4 |
                    (input[i] & 0x02) << 6 |
                    (input[i] & 0x01) << 1 );
        }

        return output;
    }

}
