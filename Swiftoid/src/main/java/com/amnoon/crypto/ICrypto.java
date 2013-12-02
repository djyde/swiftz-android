package com.amnoon.crypto;

public interface ICrypto {
    public byte[] encrypt(byte[] input);
    public byte[] decrypt(byte[] input);
}
