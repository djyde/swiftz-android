package com.amnoon.acl;

import android.support.annotation.NonNull;

import java.io.InputStream;
import java.io.OutputStream;

import us.xingrz.common.SecureStream;

public class ACLSecureStream extends SecureStream {

    public ACLSecureStream(InputStream rawInputStream, OutputStream rawOutputStream) {
        super(rawInputStream, rawOutputStream);
    }

    @Override
    protected void encrypt(@NonNull byte[] buffer, int offset, int count) {
        for (int i = offset; i < offset + count; i++) {
            buffer[i] = (byte) (0xff
                    & (buffer[i] & BIT_1) >> 4
                    | (buffer[i] & BIT_2) >> 1
                    | (buffer[i] & BIT_3) << 1
                    | (buffer[i] & BIT_4) >> 3
                    | (buffer[i] & BIT_5) << 4
                    | (buffer[i] & BIT_6) // -
                    | (buffer[i] & BIT_7) >> 1
                    | (buffer[i] & BIT_8) << 4
            );
        }
    }

    @Override
    protected void decrypt(@NonNull byte[] buffer, int byteOffset, int byteCount) {
        for (int i = byteOffset; i < byteOffset + byteCount; i++) {
            buffer[i] = (byte) (0xff
                    & (buffer[i] & BIT_1) >> 4
                    | (buffer[i] & BIT_2) >> 1
                    | (buffer[i] & BIT_3) << 1
                    | (buffer[i] & BIT_4) >> 4
                    | (buffer[i] & BIT_5) << 4
                    | (buffer[i] & BIT_6) // -
                    | (buffer[i] & BIT_7) << 3
                    | (buffer[i] & BIT_8) << 1
            );
        }
    }

}
