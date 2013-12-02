package com.amnoon.crypto;

/**
 * "crypto 3849"
 */

public class Ecbhafdg implements ICrypto {

    public byte[] encrypt(byte[] input) {
        byte[] output = new byte[input.length];

        for (int i = 0; i < input.length; i++) {
            output[i] = (byte) (
                    (input[i] & 0x80) >> 4 |
                    (input[i] & 0x40) >> 1 |
                    (input[i] & 0x20) << 1 |
                    (input[i] & 0x10) >> 3 |
                    (input[i] & 0x08) << 4 |
                    (input[i] & 0x04)      |
                    (input[i] & 0x02) >> 1 |
                    (input[i] & 0x01) << 4 );
        }

        return output;
    }

    public byte[] decrypt(byte[] input) {
        byte[] output = new byte[input.length];

        for (int i = 0; i < input.length; i++) {
            output[i] = (byte) (
                    (input[i] & 0x80) >> 4 |
                    (input[i] & 0x40) >> 1 |
                    (input[i] & 0x20) << 1 |
                    (input[i] & 0x10) >> 4 |
                    (input[i] & 0x08) << 4 |
                    (input[i] & 0x04)      |
                    (input[i] & 0x02) << 3 |
                    (input[i] & 0x01) << 1 );
        }

        return output;
    }

}
